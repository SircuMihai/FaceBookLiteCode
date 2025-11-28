# Postman JWT Token Authentication Guide

## Overview
This guide explains how to use JWT tokens in Postman to authenticate API requests for the FacebookLite application.

## Base URL
```
http://localhost:8082
```

---

## Step 1: Register a New User (Optional)

**Endpoint:** `POST /api/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
}
```

**Response:**
```json
{
    "userId": 11,
    "username": "testuser",
    "email": "test@example.com",
    "role": "USER",
    "message": "User registered successfully. Please log in to continue."
}
```

**Note:** Registration does NOT return a token. You must login after registration.

---

## Step 2: Login to Get JWT Token

**Endpoint:** `POST /api/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "username": "admin",
    "password": "your_password_here"
}
```

**Response:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 10,
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN"
}
```

**Important:** Copy the `token` value from the response. You'll need it for authenticated requests.

---

## Step 3: Using JWT Token in Postman

### Method 1: Manual Header (Recommended for Testing)

1. Create a new request in Postman
2. Go to the **Headers** tab
3. Add a new header:
   - **Key:** `Authorization`
   - **Value:** `Bearer YOUR_TOKEN_HERE`
   
   Example:
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

### Method 2: Postman Environment Variables (Recommended for Multiple Requests)

1. Click on **Environments** in the left sidebar
2. Click **+** to create a new environment
3. Add a variable:
   - **Variable:** `jwt_token`
   - **Initial Value:** (leave empty, will be set after login)
   - **Current Value:** (leave empty)
4. Save the environment
5. Select your environment from the dropdown (top right)

**After Login:**
1. In the login response, copy the token
2. Click on your environment
3. Update `jwt_token` current value with your token
4. Save

**In Requests:**
- **Key:** `Authorization`
- **Value:** `Bearer {{jwt_token}}`

### Method 3: Automatic Token Extraction (Advanced)

1. Create a **Pre-request Script** for your login request:
```javascript
pm.test("Extract token", function () {
    var jsonData = pm.response.json();
    pm.environment.set("jwt_token", jsonData.token);
});
```

2. After running the login request, the token will be automatically saved to the environment variable.

---

## Step 4: Making Authenticated Requests

### Example: Get All Users

**Endpoint:** `GET /api/users`

**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

**Response:**
```json
[
    {
        "userId": 5,
        "username": "example",
        "email": "ceva@example.com",
        "firstName": "example",
        "lastName": "example",
        "profilePicture": null,
        "lastLogin": null,
        "privateAccount": false,
        "role": "USER"
    },
    {
        "userId": 10,
        "username": "admin",
        "email": "admin@example.com",
        "firstName": "admin",
        "lastName": "admin",
        "profilePicture": null,
        "lastLogin": null,
        "privateAccount": false,
        "role": "ADMIN"
    }
]
```

### Example: Create a Post

**Endpoint:** `POST /api/posts`

**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "userId": 10,
    "content": "This is my first post!"
}
```

---

## Step 5: Validate Token

**Endpoint:** `GET /api/auth/validate`

**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

**Response (Valid Token):**
```json
{
    "valid": true,
    "username": "admin",
    "userId": 10
}
```

**Response (Invalid Token):**
```json
{
    "valid": false
}
```

---

## Step 6: Logout

**Endpoint:** `POST /api/auth/logout`

**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

**Response:**
```json
{
    "message": "User logged out successfully."
}
```

**Note:** After logout, the token is revoked and cannot be used again.

---

## Public Endpoints (No Token Required)

These endpoints don't require authentication:

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `GET /api/users` - Get all users (public)
- `GET /` - Home page
- `GET /index.html` - Index page
- Static resources (`/css/**`, `/js/**`, `/screens/**`, etc.)

---

## Protected Endpoints (Token Required)

All other endpoints require a valid JWT token:

### User Endpoints
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/username/{username}` - Get user by username
- `GET /api/users/email/{email}` - Get user by email
- `GET /api/users/private/{privateAccount}` - Get users by privacy setting
- `GET /api/users/search/firstname/{firstName}` - Search by first name
- `GET /api/users/search/lastname/{lastName}` - Search by last name

### Post Endpoints
- `GET /api/posts` - Get all posts
- `GET /api/posts/{id}` - Get post by ID
- `POST /api/posts` - Create post
- `PUT /api/posts/{id}` - Update post
- `DELETE /api/posts/{id}` - Delete post
- `GET /api/posts/user/{userId}` - Get posts by user
- `GET /api/posts/search/content/{content}` - Search posts by content
- `GET /api/posts/search/date/{date}` - Search posts by date
- `POST /api/posts/{id}/toggle-like?userId={userId}` - Toggle like
- `GET /api/posts/{id}/like-status?userId={userId}` - Get like status

### Admin Endpoints (Requires ADMIN Role)
- `GET /api/admin/**` - All admin endpoints require ADMIN role

---

## Common Errors

### 401 Unauthorized
- **Cause:** Missing or invalid token
- **Solution:** 
  1. Make sure you're including the `Authorization` header
  2. Format: `Bearer YOUR_TOKEN_HERE` (with space after "Bearer")
  3. Token might be expired (tokens expire after 24 hours)
  4. Token might have been revoked (after logout)

### 403 Forbidden
- **Cause:** Insufficient permissions (e.g., trying to access admin endpoint as regular user)
- **Solution:** Use an account with the required role (e.g., ADMIN role for admin endpoints)

### 400 Bad Request
- **Cause:** Invalid request body or missing required fields
- **Solution:** Check your JSON body matches the expected format

---

## Token Expiration

- **Default Expiration:** 24 hours (86400000 milliseconds)
- **Configuration:** Set in `application.properties` as `jwt.expiration`
- **After Expiration:** You must login again to get a new token

---

## Testing Workflow

1. **Register** a new user (or use existing: `admin` / `example`)
2. **Login** to get your JWT token
3. **Copy the token** from the response
4. **Set Authorization header** in all subsequent requests: `Bearer YOUR_TOKEN`
5. **Make authenticated requests** to protected endpoints
6. **Validate token** if needed to check if it's still valid
7. **Logout** when done (revokes the token)

---

## Quick Test Collection

### 1. Register
```
POST http://localhost:8082/api/auth/register
Content-Type: application/json

{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123"
}
```

### 2. Login
```
POST http://localhost:8082/api/auth/login
Content-Type: application/json

{
    "username": "newuser",
    "password": "password123"
}
```

### 3. Get All Users (with token)
```
GET http://localhost:8082/api/users
Authorization: Bearer YOUR_TOKEN_HERE
```

### 4. Create Post (with token)
```
POST http://localhost:8082/api/posts
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json

{
    "userId": YOUR_USER_ID,
    "content": "Hello from Postman!"
}
```

### 5. Validate Token
```
GET http://localhost:8082/api/auth/validate
Authorization: Bearer YOUR_TOKEN_HERE
```

### 6. Logout
```
POST http://localhost:8082/api/auth/logout
Authorization: Bearer YOUR_TOKEN_HERE
```

---

## Notes

- Tokens are stored in memory (JwtTokenStore) and will be lost if the server restarts
- Each login revokes all previous tokens for that user
- Only one active token per user at a time
- Admin endpoints require the user's role to be "ADMIN" in the database

