# Messages Not Working - Troubleshooting Guide

## Common Issues and Solutions

### 1. ❌ **Hardcoded API URL (Most Common Issue)**

**Problem:** The frontend uses `http://localhost:8082/api` which only works on the same machine.

**Solution:** Your friend needs to change the API URL to match the server's address.

**Fix:**
1. Open `src/main/resources/Static/js/app.js`
2. Find line 4: `this.apiBaseUrl = 'http://localhost:8082/api';`
3. Change to your server's IP address:
   - If server is on your machine: `http://YOUR_IP_ADDRESS:8082/api`
   - Example: `http://192.168.1.100:8082/api`
   - Or if using a domain: `http://yourdomain.com:8082/api`

**How to find your IP address:**
- Windows: Open CMD and run `ipconfig` → Look for "IPv4 Address"
- Mac/Linux: Run `ifconfig` or `ip addr` → Look for your network interface IP

---

### 2. ❌ **JWT Token Missing or Expired**

**Problem:** Messages require authentication. If the friend doesn't have a valid token, messages won't work.

**Symptoms:**
- Console shows "401 Unauthorized" or "Authentication required"
- Messages page loads but can't send/receive

**Solution:**
1. Make sure your friend is **logged in**
2. Check browser console (F12) for authentication errors
3. If token expired, logout and login again
4. Check that `authToken` is set in the browser's localStorage

**Check in Browser Console:**
```javascript
// Check if user is logged in
app.currentUser
app.authToken

// If null, need to login again
```

---

### 3. ❌ **Server Not Running or Not Accessible**

**Problem:** The Spring Boot server might not be running or not accessible from your friend's machine.

**Check:**
1. Is the server running on port 8082?
2. Can your friend access: `http://YOUR_IP:8082/api/users`?
3. Check firewall settings - port 8082 must be open

**Test from friend's browser:**
```
http://YOUR_IP_ADDRESS:8082/api/users
```
Should return a JSON array of users (or empty array `[]`).

---

### 4. ❌ **CORS Issues**

**Problem:** Browser blocking cross-origin requests.

**Symptoms:**
- Console shows "CORS policy" errors
- Network tab shows OPTIONS request failing

**Solution:**
- CORS is already configured to allow all origins (`*`)
- If still having issues, check browser console for specific CORS errors
- Try a different browser
- Clear browser cache

---

### 5. ❌ **Network/Firewall Issues**

**Problem:** Firewall or network blocking port 8082.

**Solution:**
1. **Windows Firewall:**
   - Go to Windows Defender Firewall
   - Allow port 8082 through firewall
   - Or temporarily disable firewall to test

2. **Router/Network:**
   - If on different networks, you may need to:
     - Port forward 8082 on your router
     - Or use a VPN/tunneling service (ngrok, etc.)

3. **Same Network:**
   - Make sure both devices are on the same network
   - Try pinging the server IP from friend's machine

---

### 6. ❌ **Database Connection Issues**

**Problem:** Friend's database might be different or not connected.

**Symptoms:**
- Messages don't save
- Can't load conversations
- Database errors in server logs

**Solution:**
- Make sure both are using the same database
- Or friend needs their own database instance
- Check `application.properties` database configuration

---

### 7. ❌ **Browser Console Errors**

**Problem:** JavaScript errors preventing messages from working.

**Solution:**
1. Open browser console (F12)
2. Look for red error messages
3. Check Network tab for failed requests
4. Share error messages for debugging

**Common errors:**
- `Failed to fetch` → Network/server issue
- `401 Unauthorized` → Token issue
- `CORS policy` → CORS issue
- `Cannot read property` → JavaScript error

---

## Quick Diagnostic Steps

### Step 1: Check API URL
```javascript
// In browser console (F12)
console.log(app.apiBaseUrl);
// Should show: http://YOUR_IP:8082/api (not localhost)
```

### Step 2: Check Authentication
```javascript
// In browser console
console.log('User:', app.currentUser);
console.log('Token:', app.authToken ? 'Present' : 'Missing');
```

### Step 3: Test API Connection
Open in browser:
```
http://YOUR_IP_ADDRESS:8082/api/users
```
Should return JSON (even if empty `[]`).

### Step 4: Test Messages Endpoint
In browser console:
```javascript
// Replace YOUR_TOKEN with actual token
fetch('http://YOUR_IP:8082/api/messages', {
    headers: {
        'Authorization': 'Bearer YOUR_TOKEN'
    }
})
.then(r => r.json())
.then(data => console.log('Messages:', data))
.catch(err => console.error('Error:', err));
```

---

## Recommended Fix: Dynamic API URL

Instead of hardcoding the URL, make it configurable:

**Option 1: Use window.location (if same domain)**
```javascript
this.apiBaseUrl = `${window.location.protocol}//${window.location.hostname}:8082/api`;
```

**Option 2: Environment-based configuration**
```javascript
// Check if running locally or on server
const hostname = window.location.hostname;
this.apiBaseUrl = hostname === 'localhost' || hostname === '127.0.0.1'
    ? 'http://localhost:8082/api'
    : `${window.location.protocol}//${hostname}:8082/api`;
```

**Option 3: Configuration file**
Create a `config.js` file that can be easily changed:
```javascript
// config.js
window.API_BASE_URL = 'http://YOUR_IP_ADDRESS:8082/api';
```

Then in `app.js`:
```javascript
this.apiBaseUrl = window.API_BASE_URL || 'http://localhost:8082/api';
```

---

## Testing Checklist

- [ ] Server is running on port 8082
- [ ] API URL is correct (not localhost for remote access)
- [ ] Friend can access `http://YOUR_IP:8082/api/users` in browser
- [ ] Friend is logged in (has valid token)
- [ ] Browser console shows no errors
- [ ] Network tab shows successful API requests
- [ ] Firewall allows port 8082
- [ ] Both on same network OR port forwarding configured
- [ ] Database is accessible
- [ ] CORS is properly configured

---

## Still Not Working?

1. **Check server logs** for errors
2. **Check browser console** (F12) for JavaScript errors
3. **Check Network tab** for failed requests
4. **Test with Postman/curl** to isolate frontend vs backend issues
5. **Compare working vs non-working setup** - what's different?

---

## Quick Fix for Your Friend

**Immediate solution:**
1. Find your computer's IP address (run `ipconfig` on Windows)
2. Tell your friend to change line 4 in `app.js`:
   ```javascript
   this.apiBaseUrl = 'http://YOUR_IP_ADDRESS:8082/api';
   ```
3. Refresh the browser
4. Make sure friend is logged in
5. Try sending a message

This should fix 90% of the issues!

