## JWT Login/Registration Tutorial (Spring Boot 3)

This repo’s Spring Boot API (`api-springboot/`) now supports **JWT-based auth**.

Textual diagram (request flow):

```text
   (1) Register / Login
Client  --------------------------->  API
        POST /api/v1/users/register
        POST /api/v1/users/login

API  --------------------------->  Client
     { token, user }


   (2) Call protected endpoints
Client  ---------------------------------------------->  API
        GET /api/v1/auth/me
        Authorization: Bearer <JWT>

API: JwtAuthenticationFilter
     - extract token from header
     - verify signature + expiration
     - set authenticated user (SecurityContext)

Controller  ------------------------------------------>  Client
            200 OK (if token valid)
            401 Unauthorized (if missing/invalid token)
```

### JWT concept (what it is + why we use it)

**JWT** stands for **JSON Web Token**. It’s a compact string that represents a user’s identity after they log in.

- **What it contains**: a JWT has *claims* (data) like a **subject** (often the username) and an **expiration time**.
- **How it’s trusted**: the server **signs** the token with a secret key. If anyone changes the token, the signature check fails.
- **How it’s used**: the client stores the token and sends it on each request in an HTTP header:
  - `Authorization: Bearer <token>`

**Why we use JWTs in APIs:**

- **Stateless authentication**: the server doesn’t need to store a session for every user. Each request “proves” who the user is by presenting a valid token.
- **Works well for REST clients**: web apps, mobile apps, and desktop clients (like your JavaFX client) can all authenticate the same way.
- **Expiration for safety**: tokens can be short-lived, limiting damage if a token is stolen.

General diagram (sessions vs JWT):

```text
Option A) Traditional server sessions (stateful)

   Login
Client  --------------------->  Server
        username + password

Server --------------------->  Client
       Set-Cookie: SESSION_ID=abc123
       (Server stores session data for abc123)

Next request
Client  --------------------->  Server
        Cookie: SESSION_ID=abc123

Server: look up abc123 in server-side session store


Option B) JWT (stateless)

   Login
Client  --------------------->  Server
        username + password

Server --------------------->  Client
       JWT (signed, expires)
       (No per-user session stored on server)

Next request
Client  --------------------->  Server
        Authorization: Bearer <JWT>

Server: verify signature + expiration, then trust the claims
```

Important: a JWT is **not encrypted** by default—it is only **signed**. That’s why you should never put secrets (like passwords) inside the token.

### What you get

- **Register**: `POST /api/v1/users/register` → returns `{ token, user }`
- **Login**: `POST /api/v1/users/login` → returns `{ token, user }`
- **Protected example**: `GET /api/v1/auth/me` (requires `Authorization: Bearer <token>`)

### 1) Dependencies added

In `api-springboot/pom.xml`:

- `spring-boot-starter-security`
- `io.jsonwebtoken:jjwt-*` (API + runtime impl + jackson)

### 2) Configuration (JWT secret + expiration)

In `api-springboot/src/main/resources/application.yml`:

```yml
app:
  security:
    jwt:
      secret: ${JWT_SECRET:change-me}
      expiration-seconds: ${JWT_EXPIRATION_SECONDS:3600}
```

Set an env var in your shell (recommended):

```bash
export JWT_SECRET="$(openssl rand -base64 32)"
export JWT_EXPIRATION_SECONDS="3600"
```

Important: for HS256, the secret must be sufficiently long. The `jjwt` library will reject too-short secrets.

### 3) How auth works (high level)

- `JwtService` creates and validates tokens.
- `JwtAuthenticationFilter` reads the `Authorization` header, validates the token, and sets the authenticated user in Spring Security’s context.
- `SecurityConfig` makes the API **stateless** and:
  - allows anonymous access to `/api/v1/users/register` and `/api/v1/users/login`
  - requires JWT auth for everything else
- `DbUserDetailsService` loads users from the `users` table (by username or email).

### 4) Password hashing

- New registrations are hashed with **BCrypt** (via Spring Security `PasswordEncoder`).
- Existing users that were stored with the project’s legacy `sha256:` format are still accepted for login.

### 5) Try it with curl

#### Register

```bash
curl -sS -X POST "http://localhost:8080/api/v1/users/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"secret123"}'
```

Response shape:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "alice",
    "email": "alice@example.com"
  }
}
```

#### Login

```bash
curl -sS -X POST "http://localhost:8080/api/v1/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"secret123"}'
```

#### Call a protected endpoint

```bash
TOKEN="(paste token here)"
curl -sS "http://localhost:8080/api/v1/auth/me" \
  -H "Authorization: Bearer $TOKEN"
```

### 6) Test coverage

`api-springboot/src/test/java/.../UserAuthIntegrationTest.java` verifies:

- register returns `token` + `user`
- login returns `token` + `user`
- `/api/v1/auth/me` works when called with the login token

