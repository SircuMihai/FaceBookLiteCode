# Check Admin Status for BenDover_x1

## Step 1: Open Browser Console

1. **Press F12** to open Developer Tools
2. Go to the **Console** tab
3. You should see debug messages when the dashboard loads

## Step 2: Check Your Current Role

Type this in the console:
```javascript
app.currentUser
```

Look for the `role` field. It should say `"ADMIN"`.

## Step 3: Check What the Console Shows

When you load the dashboard, you should see console messages like:
```
Current user: {userId: 1, username: "BenDover_x1", role: "ADMIN", ...}
User role: ADMIN
Is admin? true
```

If you see:
- `role: "USER"` or `role: null` → The user isn't an admin yet
- `role: "ADMIN"` but button still doesn't show → There's a frontend issue

## Step 4: Manually Promote (if needed)

If the role is not "ADMIN", run this in the browser console:
```javascript
fetch('http://localhost:8082/api/setup/promote/BenDover_x1', {method: 'PUT'})
  .then(r => r.json())
  .then(data => console.log('Promotion result:', data))
```

Then **logout and login again**.

## Step 5: Force Add Admin Button (Temporary Test)

To test if the button would work, try this in console:
```javascript
const btn = document.createElement('button');
btn.className = 'btn btn-primary';
btn.style.background = '#e74c3c';
btn.innerHTML = '<i class="fas fa-shield-alt"></i> Admin';
btn.onclick = () => app.showPage('admin-dashboard-page');
document.getElementById('dashboard-actions').appendChild(btn);
```

This will manually add the button so you can test if admin access works.

