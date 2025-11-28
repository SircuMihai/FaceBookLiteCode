# How to Promote BenDover_x1 to Admin

## 📍 Where the Code Lives

### 1. **Setup Endpoint** (Easiest Method)
**File:** `src/main/java/com/example/FacebookLiteCode/controller/SetupController.java`
**Lines:** 33-56

```java
@PutMapping("/promote/{username}")
public ResponseEntity<?> promoteToAdmin(@PathVariable String username) {
    Users user = usersRepository.findByUsername(username)
            .orElse(null);
    
    if (user == null) {
        return ResponseEntity.notFound().build();
    }
    
    if ("ADMIN".equals(user.getRole())) {
        return ResponseEntity.badRequest().body(Map.of("error", "User is already an admin"));
    }
    
    user.setRole("ADMIN");
    usersRepository.save(user);
    
    // Returns success response
}
```

**Endpoint URL:** `PUT http://localhost:8082/api/setup/promote/BenDover_x1`

---

## 🚀 How to Promote BenDover_x1

### **Method 1: Using cURL (Command Line)**

Open your terminal/PowerShell and run:

```bash
curl -X PUT http://localhost:8082/api/setup/promote/BenDover_x1
```

**Expected Response:**
```json
{
  "message": "User promoted to admin successfully",
  "username": "BenDover_x1",
  "userId": 1,
  "role": "ADMIN"
}
```

### **Method 2: Using Postman**

1. Open Postman
2. Create a new request
3. Set method to **PUT**
4. URL: `http://localhost:8082/api/setup/promote/BenDover_x1`
5. Click **Send**

### **Method 3: Using Browser (if you have a browser extension)**

Some browsers have REST client extensions. Use:
- **Method:** PUT
- **URL:** `http://localhost:8082/api/setup/promote/BenDover_x1`

### **Method 4: Using SQL (Direct Database)**

If the API isn't working, you can update directly in the database:

```sql
-- Connect to PostgreSQL
psql -h localhost -p 5555 -U admin -d Facebook
-- Password: admin

-- Update the user
UPDATE users SET role = 'ADMIN' WHERE username = 'BenDover_x1';

-- Verify the change
SELECT user_id, username, email, role FROM users WHERE username = 'BenDover_x1';
```

**Using Docker:**
```bash
docker exec -it FacebookLiteCode psql -U admin -d Facebook
```

Then run:
```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'BenDover_x1';
```

---

## ✅ After Promotion

1. **Logout** from the application (if logged in as BenDover_x1)
2. **Login again** - This refreshes your JWT token with the new ADMIN role
3. **Check for Admin Button** - You should see an "Admin" button in your dashboard
4. **Access Admin Dashboard** - Click the Admin button to access the admin panel

---

## 🔍 Verify It Worked

### Check via API:
```bash
curl http://localhost:8082/api/users/username/BenDover_x1
```

Look for `"role": "ADMIN"` in the response.

### Check in Database:
```sql
SELECT username, role FROM users WHERE username = 'BenDover_x1';
```

Should show: `role = 'ADMIN'`

---

## 🛠️ Troubleshooting

**If you get "Connection refused":**
- Make sure your Spring Boot application is running on port 8082
- Check: `http://localhost:8082/api/test/status`

**If you get "User not found":**
- Verify the username is exactly `BenDover_x1` (case-sensitive)
- Check the database: `SELECT username FROM users;`

**If you get "User is already an admin":**
- The user is already an admin! Just logout and login again.

**If the endpoint doesn't exist:**
- Make sure you've restarted the Spring Boot application after the changes
- Verify `SetupController.java` exists in the project

---

## 📂 File Locations Summary

| What | Where |
|------|-------|
| Setup Endpoint Code | `src/main/java/com/example/FacebookLiteCode/controller/SetupController.java` |
| Security Config | `src/main/java/com/example/FacebookLiteCode/config/SecurityConfig.java` (line 27) |
| Admin Controller | `src/main/java/com/example/FacebookLiteCode/controller/AdminController.java` (lines 119-133) |
| Frontend Functions | `src/main/resources/Static/js/app.js` (lines 1616-1662) |

---

## 🎯 Quick Command Reference

```bash
# Promote by username
curl -X PUT http://localhost:8082/api/setup/promote/BenDover_x1

# Promote by user ID (if you know the ID)
curl -X PUT http://localhost:8082/api/setup/promote-by-id/1

# Check user details
curl http://localhost:8082/api/users/username/BenDover_x1
```

