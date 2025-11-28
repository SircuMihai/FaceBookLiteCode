# Troubleshooting: Can't See Admin Dashboard

## The Problem
You logged in as Ben but can't see the admin dashboard button.

## Why This Happens

The admin button only appears if:
1. ✅ Your user has `role = 'ADMIN'` in the database
2. ✅ Your JWT token includes the ADMIN role
3. ✅ You've refreshed your session after being promoted

## Step-by-Step Fix

### Step 1: Promote Ben to Admin

**Option A: Using Setup Endpoint**
```bash
curl -X PUT http://localhost:8082/api/setup/promote/BenDover_x1
```

**Option B: Using SQL**
```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'BenDover_x1';
```

### Step 2: **IMPORTANT - Logout and Login Again!**

This is the most common issue! Your JWT token was created BEFORE you became admin, so it still has the old role.

1. **Click Logout** in the application
2. **Login again** with BenDover_x1
3. This creates a NEW JWT token with the ADMIN role

### Step 3: Check Your Role in Browser Console

Open your browser's Developer Console (F12) and type:
```javascript
app.currentUser
```

You should see:
```javascript
{
  userId: 1,
  username: "BenDover_x1",
  role: "ADMIN",  // ← This should say "ADMIN"
  ...
}
```

If it says `role: "USER"` or `role: null`, you need to logout/login again.

### Step 4: Verify in Database

Check if the role is actually set in the database:
```sql
SELECT username, role FROM users WHERE username = 'BenDover_x1';
```

Should show: `role = 'ADMIN'`

## Quick Checklist

- [ ] User promoted to ADMIN in database
- [ ] Logged out completely
- [ ] Logged back in
- [ ] Browser console shows `role: "ADMIN"`
- [ ] Admin button appears in dashboard

## Where the Admin Button Should Appear

The admin button appears in the **Dashboard** page, in the top-right area with other buttons (Profile, Friends, Messages, Logout).

**Code Location:** `src/main/resources/Static/js/app.js` line 456

```javascript
if (dashboardActions && this.currentUser.role === 'ADMIN') {
    // Admin button is added here
}
```

## Still Not Working?

### Check Browser Console for Errors
1. Press F12 to open Developer Tools
2. Go to Console tab
3. Look for any red error messages
4. Type: `app.currentUser.role` and see what it returns

### Manual Access (Temporary)
You can manually navigate to the admin dashboard by typing in the browser console:
```javascript
app.showPage('admin-dashboard-page')
```

But this won't work if you don't have admin role - you'll get a 403 error when trying to access admin endpoints.

### Verify API Access
Try accessing an admin endpoint directly:
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" http://localhost:8082/api/admin/stats
```

If you get 403 Forbidden, your token doesn't have admin role.

## Most Common Solution

**99% of the time, the fix is:**
1. Promote user to admin ✅
2. **Logout** ✅
3. **Login again** ✅

The JWT token is created during login and includes your role. If you were promoted AFTER logging in, your token still has the old role!

