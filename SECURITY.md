# Security Guide

## 🚨 **CRITICAL SECURITY ALERT**

Your database credentials have been exposed in version control. **IMMEDIATE ACTION REQUIRED:**

### **1. Change Database Password NOW**
- Go to your Render dashboard
- Navigate to your PostgreSQL database
- **CHANGE THE PASSWORD IMMEDIATELY**
- Update your local .env file with the new password

### **2. Remove Sensitive Files from Git**
```bash
# Remove application.yml from Git tracking (keeps file locally)
git rm --cached src/main/resources/application.yml

# Commit this change
git add .gitignore
git commit -m "Remove sensitive configuration files from tracking"

# If you've already pushed to remote, you may need to force push
# WARNING: This rewrites history - only do this if you haven't shared the repo
git push --force-with-lease origin main
```

## 🔒 **Current Security Measures**

### **✅ Implemented:**
- Environment variable support in application.yml
- Template file created (application.yml.template)
- .gitignore updated to exclude sensitive files
- Configuration guide created

### **⚠️ Still Need to Do:**
- Change database password
- Remove application.yml from Git history
- Create .env file with new credentials
- Test application with environment variables

## 🛡️ **Security Best Practices**

### **Never Commit:**
- Database credentials
- API keys
- Email passwords
- Private keys
- Access tokens

### **Always Use:**
- Environment variables
- .env files (not committed)
- Secrets management services
- Strong, unique passwords

## 📋 **Action Checklist**

- [ ] Change Render database password
- [ ] Remove application.yml from Git tracking
- [ ] Create .env file with new credentials
- [ ] Test application startup
- [ ] Verify no sensitive data in Git history
- [ ] Consider using secrets management service for production

## 🔍 **Verification Commands**

```bash
# Check if application.yml is still tracked
git ls-files | grep application.yml

# Check Git history for sensitive data
git log --all --full-history -- src/main/resources/application.yml

# Verify .env is ignored
git status .env
```

## 🚀 **Next Steps After Security Fix**

1. **Test Application:** Ensure it starts with environment variables
2. **Deploy Securely:** Use environment variables in production
3. **Monitor Access:** Check database logs for unauthorized access
4. **Regular Updates:** Implement regular password rotation
5. **Team Training:** Ensure all team members understand security practices 