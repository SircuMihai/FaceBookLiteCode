# Admin Dashboard Setup Guide

## How to Access Admin Dashboard

The admin dashboard is already built into the application. Once you have admin privileges, you can access it by:

1. **Logging in** with a user account that has the `ADMIN` role
2. **Navigating to the Admin Dashboard** - you'll see an "Admin" button in the dashboard if you're an admin
3. **Direct URL**: The admin dashboard page is available at the `admin-dashboard-page` route

## How to Give a User Admin Role

You have **3 options** to grant admin privileges:

### Option 1: Using the Setup Endpoint (Easiest - Recommended for First Admin)

Use the setup endpoint to promote a user to admin. This endpoint is open for initial setup:

**By Username:**
```bash
curl -X PUT http://localhost:8082/api/setup/promote/{username}
```

**By User ID:**
```bash
curl -X PUT http://localhost:8082/api/setup/promote-by-id/{userId}
```

**Example:**
```bash
# Promote user "john" to admin
curl -X PUT http://localhost:8082/api/setup/promote/john

# Or by user ID (e.g., user ID 1)
curl -X PUT http://localhost:8082/api/setup/promote-by-id/1
```

**Using Postman or Browser:**
- Method: PUT
- URL: `http://localhost:8082/api/setup/promote/yourusername`
- No authentication required

### Option 2: Using SQL (Direct Database Update)

Connect to your PostgreSQL database and run:

```sql
-- Update user by username
UPDATE users SET role = 'ADMIN' WHERE username = 'yourusername';

-- Or update by user_id
UPDATE users SET role = 'ADMIN' WHERE user_id = 1;

-- Verify the change
SELECT user_id, username, email, role FROM users WHERE username = 'yourusername';
```

**To connect to PostgreSQL:**
```bash
# Using Docker (if using docker-compose)
docker exec -it FacebookLiteCode psql -U admin -d Facebook

# Or using psql directly
psql -h localhost -p 5555 -U admin -d Facebook
# Password: admin
```

### Option 3: Using Admin Endpoint (Requires Existing Admin)

If you already have an admin user, you can use the admin endpoint to promote other users:

**Endpoint:** `PUT /api/admin/users/{userId}/promote`

**Example:**
```bash
curl -X PUT http://localhost:8082/api/admin/users/2/promote \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Demote from Admin:**
```bash
curl -X PUT http://localhost:8082/api/admin/users/{userId}/demote \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Admin Dashboard Features

Once you have admin access, you can:

1. **View Statistics:**
   - Total Users
   - Total Posts
   - Total Comments
   - Total Likes

2. **Manage Users:**
   - View all users
   - Search users
   - Delete users
   - Promote/demote users (via API)

3. **Manage Posts:**
   - View all posts
   - Delete posts

4. **System Information:**
   - Application details
   - Database information

## Security Note

⚠️ **Important:** The `/api/setup/**` endpoint is currently open (no authentication required) for initial setup. 

**For production:**
1. Remove `/api/setup/**` from PUBLIC_ENDPOINTS in `SecurityConfig.java`
2. Or add authentication/authorization to the SetupController
3. Or delete the SetupController entirely after creating your first admin

## Quick Start Example

1. **Create a user account** (if you don't have one):
   - Register through the signup page
   - Or use the registration endpoint

2. **Promote to admin:**
   ```bash
   curl -X PUT http://localhost:8082/api/setup/promote/yourusername
   ```

3. **Logout and login again** (to refresh your JWT token with new role)

4. **Access admin dashboard:**
   - You'll see an "Admin" button in your dashboard
   - Click it to access the admin panel

## Troubleshooting

**Can't see Admin button?**
- Make sure you logged out and logged back in after being promoted to admin
- Check that your user's role is "ADMIN" in the database
- Verify your JWT token includes the admin role

**403 Forbidden when accessing admin endpoints?**
- Your JWT token might be cached with the old role
- Logout and login again to get a new token
- Verify the user role in the database

**Setup endpoint not working?**
- Check that the endpoint is in PUBLIC_ENDPOINTS
- Verify the application is running on port 8082
- Check the username/userId exists in the database

