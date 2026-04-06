# SecureVault Deployment & Connection Guide

## ✅ Current Status

You have successfully configured your frontend to use **dynamic API endpoints** instead of hardcoded localhost URLs. 

- ✅ `user_login.html` - Updated to use `API_BASE` (2 locations)
- ✅ `auth.html` - Updated to use `API_BASE`  
- ✅ `dashboard.html` - Updated to use `API_BASE`
- ✅ `admin-portal.html` - Updated to use `API_BASE`
- ✅ `config.js` - Created with automatic environment detection

---

## ❌ The Problem

When you deploy the frontend to **Vercel** and try to login from another device:
- Frontend is running on: `https://your-app.vercel.app`
- Frontend tries to connect to `localhost:8083` → **FAILS** ❌
- `localhost` from Vercel = Vercel's servers, not your backend

---

## ✅ The Solutions

### **Option 1: Deploy Backend to Cloud** (RECOMMENDED FOR PRODUCTION)

#### A. Using **Railway** (Easiest)
1. **Push your backend repo to GitHub**
2. Go to [railway.app](https://railway.app)
3. Click "New Project" → Connect GitHub → Select your repo
4. Railway auto-detects `pom.xml` → Builds Spring Boot app
5. Get your backend URL: `https://your-app-xyz.railway.app`
6. Update `config.js`:
   ```javascript
   return 'https://your-app-xyz.railway.app';
   ```
7. Redeploy frontend to Vercel

#### B. Using **Render** (Also Easy)
1. Go to [render.com](https://render.com)
2. Create new "Web Service"
3. Connect GitHub repo
4. Configure: Java 17, `mvn clean install`
5. Get URL, update `config.js`

#### C. Using **AWS/Heroku/Azure**
- Similar process - deploy Spring Boot, get public URL, update config

---

### **Option 2: Use ngrok for Testing** (TEMPORARY)

**On your local machine** (where backend runs):
```bash
cd d:\Data Masking\BACKEND1\secure_vault
mvn spring-boot:run &
ngrok http 8083
```

You'll get: `https://abc-123-xyz.ngrok.io`

Update `config.js`:
```javascript
return 'https://abc-123-xyz.ngrok.io';
```

⚠️ **Note:** ngrok URLs change each time you restart

---

### **Option 3: CORS with Backend Running Locally** (FOR LOCAL TESTING ONLY)

Your backend already has `@CrossOrigin(origins = "*")` ✅

But for remote access from Vercel → local backend, you need:
- **Backend exposed to internet** (use ngrok Option 2)
- **Port 8083 accessible** from outside your network (usually blocked by firewall)

---

## 🔧 Setup Instructions by Scenario

### **Scenario A: Testing Locally (Same Device)**
```
✅ Works already
- Frontend: http://localhost:3000 (or open user_login.html in browser)
- Backend: http://localhost:8083
- config.js automatically detects localhost
```

### **Scenario B: Testing from Different Device on Same Network**
```
1. Find your machine's local IP:
   - Windows: ipconfig → IPv4 Address (e.g., 192.168.x.x)
   
2. Update config.js:
   return 'http://192.168.x.x:8083';
   
3. Ensure firewall allows port 8083
4. Test other device: http://192.168.x.x:8000/user_login.html
```

### **Scenario C: Production (Frontend on Vercel)**
```
1. Deploy Backend to Cloud (Railway/Render/AWS)
   └─ Get URL: https://your-backend.app
   
2. Update config.js:
   return 'https://your-backend.app';
   
3. Redeploy frontend to Vercel
   
4. Test: https://your-app.vercel.app
```

---

## 📋 Quick Deployment Checklist

- [ ] Backend running locally? ✅ Confirmed
- [ ] Frontend uses `config.js`? ✅ Done
- [ ] Decided on deployment option?
  - [ ] Option 1: Cloud deployment (Railway/Render)
  - [ ] Option 2: ngrok for testing
  - [ ] Option 3: Local network testing

---

## 🛠️ Backend Configuration

Your `application.properties`:
```properties
server.port=8083
spring.datasource.url=jdbc:mysql://localhost:3306/securevault
spring.datasource.username=root
spring.datasource.password=Akhil@2006
```

⚠️ **IMPORTANT FOR PRODUCTION:**
- Never commit passwords to git!
- Use environment variables for sensitive data
- Example for Railway:
  ```bash
  DATABASE_URL=mysql://user:pass@host/db
  ```

---

## 🚀 Next Steps

1. **Choose your deployment option** (Railway recommended)
2. **Get your backend public URL**
3. **Update `config.js` by replacing**: `'https://your-backend-domain.com'`
4. **Redeploy frontend** to Vercel
5. **Test from another device** at your Vercel URL

---

## ❓ Still Getting "Connection to Server Failed"?

### Debugging Steps:
1. **Check backend is running**
   ```bash
   curl http://localhost:8083/auth/login
   ```

2. **Check CORS headers** (open Inspector → Network tab):
   - Look for `Access-Control-Allow-Origin: *` ✅

3. **Check config.js** is loaded:
   ```javascript
   // In browser console:
   console.log(API_BASE);
   ```

4. **Check network in DevTools**:
   - Network tab → Find `/auth/login` request
   - Check Status: should be 200, 401, or 500 (not blocked by CORS)
   - If request is blocked: red text or "blocked by client"

5. **If using ngrok/remote backend**:
   - Whitelist your IP in firewall
   - Ensure CORS is enabled on backend (✅ already done)

---

## 📚 Additional Resources

- [Spring Boot CORS Documentation](https://spring.io/blog/2015/06/08/cors-in-spring-framework)
- [Railway Deployment Guide](https://docs.railway.app/guides/start)  
- [ngrok Documentation](https://ngrok.com/docs)
- [Vercel Deployment Guide](https://vercel.com/docs)

