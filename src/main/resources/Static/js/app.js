// FacebookLite Frontend Application
class FacebookLiteApp {
    constructor() {
        this.apiBaseUrl = 'http://localhost:8082/api';
        this.currentUser = null;
        this.authToken = null;
        this.init();
    }

    async init() {
        this.setupEventListeners();
        await this.checkAuthStatus();
        // Show home page initially (it's already in the DOM)
        this.showPage('home-page');
    }

    setAuthToken(token) {
        this.authToken = token;
    }

    async authFetch(url, options = {}, requiresAuth = true) {
        const headers = { ...(options.headers || {}) };

        if (options.body && !headers['Content-Type']) {
            headers['Content-Type'] = 'application/json';
        }

        if (requiresAuth) {
            if (!this.authToken) {
                throw new Error('Authentication required');
            }
            headers['Authorization'] = `Bearer ${this.authToken}`;
        }

        const response = await fetch(url, { ...options, headers });

        if (response.status === 401 || response.status === 403) {
            this.handleUnauthorized();
            throw new Error('Unauthorized');
        }

        return response;
    }

    handleUnauthorized() {
        this.logout(false);
        this.showToast('Session expired. Please log in again.', 'error');
    }

    async fetchCurrentUserProfile(userId) {
        const response = await this.authFetch(`${this.apiBaseUrl}/users/${userId}`);
        if (!response.ok) {
            throw new Error('Failed to load user profile');
        }
        const user = await response.json();
        this.currentUser = user;
        return user;
    }

    setupEventListeners() {
        this.attachEventListeners();
    }

    attachEventListeners() {
        // Navigation toggle for mobile
        const navToggle = document.getElementById('nav-toggle');
        const navMenu = document.getElementById('nav-menu');
        
        if (navToggle && !navToggle.hasAttribute('data-listener-attached')) {
            navToggle.setAttribute('data-listener-attached', 'true');
            navToggle.addEventListener('click', () => {
                navMenu.classList.toggle('active');
            });
        }

        // Login form
        const loginForm = document.getElementById('login-form');
        if (loginForm && !loginForm.hasAttribute('data-listener-attached')) {
            loginForm.setAttribute('data-listener-attached', 'true');
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }

        // Sign up form
        const signupForm = document.getElementById('signup-form');
        if (signupForm && !signupForm.hasAttribute('data-listener-attached')) {
            signupForm.setAttribute('data-listener-attached', 'true');
            signupForm.addEventListener('submit', (e) => this.handleSignUp(e));
        }

        // Post form
        const postForm = document.getElementById('post-form');
        if (postForm && !postForm.hasAttribute('data-listener-attached')) {
            postForm.setAttribute('data-listener-attached', 'true');
            postForm.addEventListener('submit', (e) => this.handleCreatePost(e));
        }

        // Edit profile form
        const editProfileForm = document.getElementById('edit-profile-form');
        if (editProfileForm && !editProfileForm.hasAttribute('data-listener-attached')) {
            editProfileForm.setAttribute('data-listener-attached', 'true');
            editProfileForm.addEventListener('submit', (e) => this.handleEditProfile(e));
        }

        // Message form
        const messageForm = document.getElementById('message-form');
        if (messageForm && !messageForm.hasAttribute('data-listener-attached')) {
            messageForm.setAttribute('data-listener-attached', 'true');
            messageForm.addEventListener('submit', (e) => this.handleSendMessage(e));
        }
    }

    // Navigation Functions
    async showPage(pageId) {
        // Check admin access
        if (pageId === 'admin-dashboard-page' && (!this.currentUser || this.currentUser.role !== 'ADMIN')) {
            this.showToast('Access denied. Admin privileges required.', 'error');
            return;
        }

        // Hide all pages
        const pages = document.querySelectorAll('.page');
        pages.forEach(page => page.classList.remove('active'));

        // Show selected page
        const targetPage = document.getElementById(pageId);
        if (targetPage) {
            targetPage.classList.add('active');
        } else {
            console.error(`Page element not found: ${pageId}`);
            this.showToast(`Error: Could not load ${pageId}`, 'error');
            return;
        }

        // Update navigation
        this.updateNavigation();

        // Reattach event listeners (in case forms were recreated)
        this.attachEventListeners();

        // Load page-specific content
        if (pageId === 'dashboard-page' && this.currentUser) {
            await this.loadDashboard();
        } else if (pageId === 'profile-page' && this.currentUser) {
            await this.loadProfile();
        } else if (pageId === 'friends-page' && this.currentUser) {
            await this.loadFriends();
        } else if (pageId === 'messages-page' && this.currentUser) {
            await this.loadMessages();
        } else if (pageId === 'admin-dashboard-page' && this.currentUser && this.currentUser.role === 'ADMIN') {
            await this.loadAdminDashboard();
        }
    }

    updateNavigation() {
        const navLinks = document.getElementById('nav-links');
        const navAuth = document.getElementById('nav-auth');

        if (this.currentUser) {
            // User is logged in
            let links = `
                <a href="#" onclick="app.showPage('dashboard-page')">Dashboard</a>
                <a href="#" onclick="app.showPage('dashboard-page')">Posts</a>
            `;
            
            // Add admin link if user is admin
            if (this.currentUser.role === 'ADMIN') {
                links += `<a href="#" onclick="app.showPage('admin-dashboard-page')">Admin</a>`;
            }
            
            navLinks.innerHTML = links;
            navAuth.innerHTML = `
                <span>Welcome, ${this.currentUser.username}</span>
                <button class="btn btn-secondary" onclick="app.logout()">Logout</button>
            `;
        } else {
            // User is not logged in
            navLinks.innerHTML = '';
            navAuth.innerHTML = `
                <button class="btn btn-primary" onclick="app.showLogin()">Login</button>
                <button class="btn btn-secondary" onclick="app.showSignUp()">Sign Up</button>
            `;
        }
    }

    // Authentication Functions
    async handleLogin(e) {
        e.preventDefault();
        this.showLoading(true);

        const formData = new FormData(e.target);
        const loginData = {
            username: formData.get('username'),
            password: formData.get('password')
        };

        // Client-side validation
        if (!loginData.username || loginData.username.trim() === '') {
            this.showToast('Please enter your username', 'error');
            this.showLoading(false);
            return;
        }
        
        if (!loginData.password || loginData.password === '') {
            this.showToast('Please enter your password', 'error');
            this.showLoading(false);
            return;
        }

        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/auth/login`, {
                method: 'POST',
                body: JSON.stringify({
                    username: loginData.username.trim(),
                    password: loginData.password
                })
            }, false);

            if (!response.ok) {
                // Try to parse JSON error response
                let errorMessage = 'Invalid username or password. Please try again.';
                try {
                    const errorData = await response.json();
                    // Handle different error response formats
                    if (errorData.error) {
                        errorMessage = errorData.error;
                    } else if (errorData.message) {
                        errorMessage = errorData.message;
                    } else if (typeof errorData === 'string') {
                        errorMessage = errorData;
                    }
                } catch (parseError) {
                    // If JSON parsing fails, try text
                    const errorText = await response.text().catch(() => '');
                    if (errorText) {
                        try {
                            const parsed = JSON.parse(errorText);
                            errorMessage = parsed.error || parsed.message || errorText;
                        } catch {
                            errorMessage = errorText || errorMessage;
                        }
                    }
                }
                this.showToast(errorMessage, 'error');
                this.showLoading(false);
                return;
            }

            const loginResult = await response.json();
            this.setAuthToken(loginResult.token);

            try {
                await this.fetchCurrentUserProfile(loginResult.userId);
            } catch (profileError) {
                console.error('Failed to load full profile:', profileError);
                this.currentUser = {
                    userId: loginResult.userId,
                    username: loginResult.username,
                    email: loginResult.email,
                    firstName: loginResult.firstName,
                    lastName: loginResult.lastName,
                    role: loginResult.role
                };
            }

            this.showToast('Login successful!', 'success');
            // Update navigation first
            this.updateNavigation();
            // Then show dashboard
            await this.showPage('dashboard-page');
        } catch (error) {
            console.error('Login error:', error);
            let errorMessage = 'Login failed. Please try again.';
            if (error.message) {
                if (error.message.includes('Unauthorized') || error.message.includes('401')) {
                    errorMessage = 'Invalid username or password. Please check your credentials.';
                } else if (error.message.includes('Network') || error.message.includes('fetch')) {
                    errorMessage = 'Network error. Please check your connection and try again.';
                } else {
                    errorMessage = 'Login failed: ' + error.message;
                }
            }
            this.showToast(errorMessage, 'error');
        } finally {
            this.showLoading(false);
        }
    }

    async handleSignUp(e) {
        e.preventDefault();
        this.showLoading(true);

        const formData = new FormData(e.target);
        const userData = {
            username: formData.get('username'),
            email: formData.get('email'),
            password: formData.get('password'),
            firstName: formData.get('firstName'),
            lastName: formData.get('lastName')
        };

        // Client-side validation
        if (!userData.username || userData.username.trim() === '') {
            this.showToast('Please enter a username', 'error');
            this.showLoading(false);
            return;
        }
        
        if (userData.username.trim().length < 3) {
            this.showToast('Username must be at least 3 characters long', 'error');
            this.showLoading(false);
            return;
        }
        
        if (!userData.email || userData.email.trim() === '') {
            this.showToast('Please enter an email address', 'error');
            this.showLoading(false);
            return;
        }
        
        // Basic email validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(userData.email.trim())) {
            this.showToast('Please enter a valid email address', 'error');
            this.showLoading(false);
            return;
        }
        
        if (!userData.password || userData.password === '') {
            this.showToast('Please enter a password', 'error');
            this.showLoading(false);
            return;
        }
        
        if (userData.password.length < 6) {
            this.showToast('Password must be at least 6 characters long', 'error');
            this.showLoading(false);
            return;
        }

        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/auth/register`, {
                method: 'POST',
                body: JSON.stringify({
                    username: userData.username.trim(),
                    email: userData.email.trim(),
                    password: userData.password,
                    firstName: userData.firstName ? userData.firstName.trim() : null,
                    lastName: userData.lastName ? userData.lastName.trim() : null
                })
            }, false);

            if (response.ok) {
                const result = await response.json().catch(() => ({}));
                this.showToast(result.message || 'Account created successfully! Please log in.', 'success');
                e.target.reset();
                this.showLogin();
            } else {
                // Try to parse JSON error response
                let errorMessage = 'Registration failed. Please try again.';
                try {
                    const errorData = await response.json();
                    // Handle different error response formats
                    if (errorData.error) {
                        errorMessage = errorData.error;
                    } else if (errorData.message) {
                        errorMessage = errorData.message;
                    } else if (typeof errorData === 'string') {
                        errorMessage = errorData;
                    }
                } catch (parseError) {
                    // If JSON parsing fails, try text
                    const errorText = await response.text().catch(() => '');
                    if (errorText) {
                        try {
                            const parsed = JSON.parse(errorText);
                            errorMessage = parsed.error || parsed.message || errorText;
                        } catch {
                            errorMessage = errorText || errorMessage;
                        }
                    }
                }
                this.showToast(errorMessage, 'error');
            }
        } catch (error) {
            console.error('Registration error:', error);
            let errorMessage = 'Registration failed. Please try again.';
            if (error.message) {
                if (error.message.includes('Network') || error.message.includes('fetch')) {
                    errorMessage = 'Network error. Please check your connection and try again.';
                } else if (error.message.includes('400') || error.message.includes('Bad Request')) {
                    errorMessage = 'Invalid registration data. Please check all fields and try again.';
                } else {
                    errorMessage = 'Registration failed: ' + error.message;
                }
            }
            this.showToast(errorMessage, 'error');
        } finally {
            this.showLoading(false);
        }
    }

    async logout(showMessage = true) {
        if (this.authToken) {
            try {
                await this.authFetch(`${this.apiBaseUrl}/auth/logout`, {
                method: 'POST'
                });
            } catch (err) {
                console.warn('Failed to inform server about logout', err);
            }
        }

        this.currentUser = null;
        this.setAuthToken(null);
        if (showMessage) {
            this.showToast('Logged out successfully', 'success');
        }
        this.showPage('home-page');
    }

    async checkAuthStatus() {
        if (this.authToken && this.currentUser) {
            try {
                const response = await this.authFetch(`${this.apiBaseUrl}/auth/validate`);
                const validation = await response.json();
                if (!validation.valid) {
                    this.handleUnauthorized();
                }
            } catch (error) {
                console.error('Token validation failed:', error);
            }
        }
    }

    // Page Navigation Functions
    showLogin() {
        this.showPage('login-page');
    }

    showSignUp() {
        this.showPage('signup-page');
    }

    // Dashboard Functions
    async loadDashboard() {
        if (!this.currentUser) return;

        // Update user name in dashboard
        const userNameElement = document.getElementById('user-name');
        if (userNameElement) {
            userNameElement.textContent = this.currentUser.username;
        }

        // Add admin button if user is admin
        const dashboardActions = document.getElementById('dashboard-actions');
        if (dashboardActions && this.currentUser.role === 'ADMIN') {
            // Check if admin button already exists
            const existingAdminBtn = dashboardActions.querySelector('[onclick*="admin-dashboard"]');
            if (!existingAdminBtn) {
                const adminBtn = document.createElement('button');
                adminBtn.className = 'btn btn-primary';
                adminBtn.style.background = '#e74c3c';
                adminBtn.innerHTML = '<i class="fas fa-shield-alt"></i> Admin';
                adminBtn.onclick = () => app.showPage('admin-dashboard-page');
                dashboardActions.insertBefore(adminBtn, dashboardActions.lastElementChild);
            }
        }

        // Load posts
        await this.loadPosts();
    }

    // Profile Functions
    async loadProfile() {
        if (!this.currentUser) return;

        // Update profile header
        document.getElementById('profile-username').textContent = this.currentUser.username;
        document.getElementById('profile-email').textContent = this.currentUser.email;
        document.getElementById('profile-name').textContent = `${this.currentUser.firstName || ''} ${this.currentUser.lastName || ''}`.trim();

        // Update profile details
        document.getElementById('profile-username-detail').textContent = this.currentUser.username;
        document.getElementById('profile-email-detail').textContent = this.currentUser.email;
        document.getElementById('profile-firstname-detail').textContent = this.currentUser.firstName || 'Not set';
        document.getElementById('profile-lastname-detail').textContent = this.currentUser.lastName || 'Not set';
        document.getElementById('profile-account-type').textContent = this.currentUser.privateAccount ? 'Private' : 'Public';
        document.getElementById('profile-last-login').textContent = this.currentUser.lastLogin || 'Never';

        // Load user's posts
        await this.loadUserPosts();
    }

    async loadUserPosts() {
        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/posts/user/${this.currentUser.userId}`);
            if (!response.ok) {
                throw new Error('Failed to load user posts');
            }
            const posts = await response.json();
            this.displayUserPosts(posts);
        } catch (error) {
            console.error('Error loading user posts:', error);
            // Fallback to all posts if user-specific endpoint doesn't exist
            await this.loadPosts();
        }
    }

    displayUserPosts(posts) {
        const userPostsContainer = document.getElementById('user-posts');
        if (!userPostsContainer) return;

        if (posts.length === 0) {
            userPostsContainer.innerHTML = '<p class="text-center">No posts yet. Create your first post!</p>';
            return;
        }

        userPostsContainer.innerHTML = posts.map(post => `
            <div class="user-post-item">
                <div class="user-post-content">${post.content}</div>
                <div class="user-post-date">${this.formatDate(post.createdAt)}</div>
            </div>
        `).join('');
    }

    async loadPosts() {
        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/posts`);
            if (!response.ok) {
                throw new Error('Failed to load posts');
            }
            const posts = await response.json();
            this.displayPosts(posts);
        } catch (error) {
            console.error('Error loading posts:', error);
            this.showToast('Failed to load posts', 'error');
        }
    }

    displayPosts(posts) {
        const postsFeed = document.getElementById('posts-feed');
        if (!postsFeed) return;

        // Handle case where posts might not be an array
        if (!Array.isArray(posts)) {
            console.error('Posts is not an array:', posts);
            postsFeed.innerHTML = '<p class="text-center">Error loading posts. Please try again.</p>';
            return;
        }

        if (posts.length === 0) {
            postsFeed.innerHTML = '<p class="text-center">No posts yet. Create the first one!</p>';
            return;
        }

        postsFeed.innerHTML = posts.map(post => `
            <div class="post-item">
                <div class="post-header">
                    <span class="post-author">${post.username || 'Unknown User'}</span>
                    <span class="post-date">${this.formatDate(post.createdAt)}</span>
                </div>
                <div class="post-content">${post.content}</div>
                <div class="post-stats">
                    <span class="likes-count">${post.likesCount || 0} likes</span>
                    <span class="comments-count">${post.comments ? post.comments.length : 0} comments</span>
                </div>
                <div class="post-actions">
                    <button class="post-action like-btn" id="like-btn-${post.postId}" onclick="app.toggleLike(${post.postId})">
                        <i class="fas fa-heart"></i> <span class="like-text">Like</span>
                    </button>
                    <button class="post-action" onclick="app.toggleComments(${post.postId})">
                        <i class="fas fa-comment"></i> Comment
                    </button>
                </div>
                <div class="post-comments" id="comments-${post.postId}" style="display: none;">
                    <div class="comments-list" id="comments-list-${post.postId}">
                        ${post.comments ? post.comments.map(comment => `
                            <div class="comment-item">
                                <strong>${comment.username}:</strong> ${comment.content}
                                ${comment.userId === this.currentUser?.userId ? `<button class="delete-comment-btn" onclick="app.deleteComment(${comment.commentId})">×</button>` : ''}
                            </div>
                        `).join('') : ''}
                    </div>
                    <div class="comment-form">
                        <input type="text" id="comment-input-${post.postId}" placeholder="Write a comment (max 70 characters)..." class="comment-input" maxlength="70">
                        <div class="word-count" id="word-count-${post.postId}">0/70 characters</div>
                        <button onclick="app.addComment(${post.postId})" class="btn btn-primary btn-small">Post</button>
                    </div>
                </div>
            </div>
        `).join('');
        
        // Load like status for each post
        if (this.currentUser) {
            posts.forEach(post => {
                this.loadLikeStatus(post.postId);
            });
        }
    }

    async handleCreatePost(e) {
        e.preventDefault();
        if (!this.currentUser) {
            this.showToast('Please log in to create posts', 'error');
            return;
        }

        const content = document.getElementById('post-content').value;
        if (!content.trim()) {
            this.showToast('Please enter some content', 'error');
            return;
        }

        this.showLoading(true);

        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/simple/create-post`, {
                method: 'POST',
                body: JSON.stringify({
                    content: content,
                    userId: this.currentUser.userId
                })
            });

            if (response.ok) {
                const result = await response.json();
                this.showToast('Post created successfully!', 'success');
                document.getElementById('post-content').value = '';
                await this.loadPosts(); // Reload posts
            } else {
                const error = await response.text();
                this.showToast('Failed to create post: ' + error, 'error');
            }
        } catch (error) {
            console.error('Error creating post:', error);
            this.showToast('Failed to create post', 'error');
        } finally {
            this.showLoading(false);
        }
    }

    // Utility Functions
    showLoading(show) {
        const loading = document.getElementById('loading');
        if (loading) {
            loading.classList.toggle('hidden', !show);
        }
    }

    showToast(message, type = 'success') {
        const container = document.getElementById('toast-container');
        if (!container) return;

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.textContent = message;

        container.appendChild(toast);

        // Remove toast after 3 seconds
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 3000);
    }

    formatDate(dateString) {
        if (!dateString) return 'Unknown date';
        const date = new Date(dateString);
        return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
    }

    // Profile Modal Functions
    editProfile() {
        if (!this.currentUser) return;

        // Populate form with current user data
        document.getElementById('edit-firstname').value = this.currentUser.firstName || '';
        document.getElementById('edit-lastname').value = this.currentUser.lastName || '';
        document.getElementById('edit-email').value = this.currentUser.email || '';
        document.getElementById('edit-username').value = this.currentUser.username || '';
        document.getElementById('edit-private').checked = this.currentUser.privateAccount || false;

        // Show modal
        document.getElementById('edit-profile-modal').classList.remove('hidden');
    }

    closeEditProfile() {
        document.getElementById('edit-profile-modal').classList.add('hidden');
    }

    async handleEditProfile(e) {
        e.preventDefault();
        if (!this.currentUser) return;

        this.showLoading(true);

        const formData = new FormData(e.target);
        const updateData = {
            firstName: formData.get('firstName'),
            lastName: formData.get('lastName'),
            email: formData.get('email'),
            username: formData.get('username'),
            privateAccount: formData.get('privateAccount') === 'on'
        };

        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/users/${this.currentUser.userId}`, {
                method: 'PUT',
                body: JSON.stringify(updateData)
            });

            if (response.ok) {
                const updatedUser = await response.json();
                this.currentUser = updatedUser;
                this.showToast('Profile updated successfully!', 'success');
                this.closeEditProfile();
                this.loadProfile(); // Reload profile page
            } else {
                const error = await response.text();
                this.showToast('Failed to update profile: ' + error, 'error');
            }
        } catch (error) {
            console.error('Error updating profile:', error);
            this.showToast('Failed to update profile', 'error');
        } finally {
            this.showLoading(false);
        }
    }

    // Friends Functions
    async loadFriends() {
        if (!this.currentUser) return;

        // Load friend requests
        await this.loadFriendRequests();
        
        // Load current friends
        await this.loadCurrentFriends();
    }

    async searchFriends() {
        const searchTerm = document.getElementById('friend-search').value.trim();
        if (!searchTerm) {
            this.showToast('Please enter a search term', 'error');
            return;
        }

        try {
            // Get all users and current friends
            const [usersResponse, friendsResponse] = await Promise.all([
                this.authFetch(`${this.apiBaseUrl}/users`),
                this.authFetch(`${this.apiBaseUrl}/friendships/friends/${this.currentUser.userId}`)
            ]);
            
            if (!usersResponse.ok) {
                console.error('Failed to fetch users:', usersResponse.status);
                this.showToast('Failed to search users', 'error');
                return;
            }
            
            const allUsers = await usersResponse.json();
            const currentFriends = friendsResponse.ok ? await friendsResponse.json() : [];
            
            console.log('All users:', allUsers);
            console.log('Current friends:', currentFriends);
            
            // Get friend IDs to exclude from search
            const friendIds = new Set();
            currentFriends.forEach(friendship => {
                if (friendship.user1 && friendship.user1.userId !== this.currentUser.userId) {
                    friendIds.add(friendship.user1.userId);
                }
                if (friendship.user2 && friendship.user2.userId !== this.currentUser.userId) {
                    friendIds.add(friendship.user2.userId);
                }
            });
            
            // Filter users based on search term and exclude current friends and self
            const filteredUsers = allUsers.filter(user => 
                user.userId !== this.currentUser.userId && // Don't show self
                !friendIds.has(user.userId) && // Don't show current friends
                (user.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
                (user.firstName && user.firstName.toLowerCase().includes(searchTerm.toLowerCase())) ||
                (user.lastName && user.lastName.toLowerCase().includes(searchTerm.toLowerCase())))
            );
            
            console.log('Filtered users:', filteredUsers);
            this.displaySearchResults(filteredUsers);
            
        } catch (error) {
            console.error('Error searching friends:', error);
            this.showToast('Error searching for friends', 'error');
        }
    }

    displaySearchResults(users) {
        const resultsContainer = document.getElementById('search-results');
        if (!resultsContainer) return;

        if (users.length === 0) {
            resultsContainer.innerHTML = '<p>No users found matching your search.</p>';
            return;
        }

        resultsContainer.innerHTML = users.map(user => `
            <div class="user-card">
                <div class="user-info">
                    <h4>${user.firstName || ''} ${user.lastName || ''}</h4>
                    <p>@${user.username}</p>
                </div>
                <div class="user-actions">
                    <button class="btn btn-primary btn-small" onclick="app.sendFriendRequest(${user.userId})">
                        Send Friend Request
                    </button>
                </div>
            </div>
        `).join('');
    }

    async testSearch() {
        try {
            console.log('Testing search - fetching all users...');
            const [usersResponse, friendsResponse] = await Promise.all([
                this.authFetch(`${this.apiBaseUrl}/users`),
                this.authFetch(`${this.apiBaseUrl}/friendships/friends/${this.currentUser.userId}`)
            ]);
            
            if (!usersResponse.ok) {
                console.error('Failed to fetch users:', usersResponse.status);
                this.showToast('Failed to fetch users', 'error');
                return;
            }
            
            const allUsers = await usersResponse.json();
            const currentFriends = friendsResponse.ok ? await friendsResponse.json() : [];
            
            // Get friend IDs to exclude from search
            const friendIds = new Set();
            currentFriends.forEach(friendship => {
                if (friendship.user1 && friendship.user1.userId !== this.currentUser.userId) {
                    friendIds.add(friendship.user1.userId);
                }
                if (friendship.user2 && friendship.user2.userId !== this.currentUser.userId) {
                    friendIds.add(friendship.user2.userId);
                }
            });
            
            // Filter out current friends and self
            const availableUsers = allUsers.filter(user => 
                user.userId !== this.currentUser.userId && 
                !friendIds.has(user.userId)
            );
            
            console.log('All users found:', allUsers);
            console.log('Current friends:', currentFriends);
            console.log('Available users (not friends):', availableUsers);
            this.displaySearchResults(availableUsers);
            
        } catch (error) {
            console.error('Error in test search:', error);
            this.showToast('Error in test search', 'error');
        }
    }

    async sendFriendRequest(userId) {
        if (!this.currentUser) return;

        try {
            console.log('Sending friend request from', this.currentUser.userId, 'to', userId);
            const response = await this.authFetch(`${this.apiBaseUrl}/friendships/friend-request`, {
                method: 'POST',
                body: JSON.stringify({
                    user1Id: this.currentUser.userId,
                    user2Id: userId,
                    status: 'pending'
                })
            });

            if (response.ok) {
                const result = await response.json();
                console.log('Friend request sent successfully:', result);
                this.showToast('Friend request sent!', 'success');
                // Refresh the search results
                this.searchFriends();
            } else {
                const errorText = await response.text();
                console.error('Failed to send friend request:', response.status, errorText);
                this.showToast('Failed to send friend request: ' + errorText, 'error');
            }
        } catch (error) {
            console.error('Error sending friend request:', error);
            this.showToast('Error sending friend request', 'error');
        }
    }

    async loadFriendRequests() {
        if (!this.currentUser) return;

        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/friendships/requests/${this.currentUser.userId}`);
            if (!response.ok) {
                console.error('Failed to fetch friend requests:', response.status);
                return;
            }
            const requests = await response.json();
            console.log('Friend requests received:', requests);
            this.displayFriendRequests(requests);
        } catch (error) {
            console.error('Error loading friend requests:', error);
        }
    }

    displayFriendRequests(requests) {
        const container = document.getElementById('friend-requests');
        if (!container) return;

        if (!requests || requests.length === 0) {
            container.innerHTML = '<p>No pending friend requests.</p>';
            return;
        }

        container.innerHTML = requests.map(request => {
            // Handle different possible response structures
            const user = request.user1 || request.user;
            const friendshipId = request.friendshipId || request.id || request.frienshipId;
            
            if (!friendshipId) {
                console.error('Friendship ID not found in request:', request);
                return '';
            }
            
            const firstName = user ? (user.firstName || '') : '';
            const lastName = user ? (user.lastName || '') : '';
            const username = user ? (user.username || 'Unknown') : 'Unknown';
            
            return `
                <div class="user-card">
                    <div class="user-info">
                        <h4>${firstName} ${lastName}</h4>
                        <p>@${username}</p>
                    </div>
                    <div class="user-actions">
                        <button class="btn btn-primary btn-small" onclick="app.acceptFriendRequest(${friendshipId})">
                            Accept
                        </button>
                        <button class="btn btn-secondary btn-small" onclick="app.declineFriendRequest(${friendshipId})">
                            Decline
                        </button>
                    </div>
                </div>
            `;
        }).filter(html => html !== '').join('');
    }

    async acceptFriendRequest(friendshipId) {
        if (!this.currentUser) {
            this.showToast('Please log in to accept friend requests', 'error');
            return;
        }

        if (!friendshipId || friendshipId === 0) {
            this.showToast('Invalid friend request ID', 'error');
            return;
        }

        console.log('Accepting friend request with ID:', friendshipId);

        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/friendships/${friendshipId}/accept`, {
                method: 'PUT'
            });

            if (response.ok) {
                this.showToast('Friend request accepted!', 'success');
                await this.loadFriendRequests();
                await this.loadCurrentFriends();
            } else {
                // Try to get error message
                let errorMessage = 'Failed to accept friend request';
                try {
                    const errorData = await response.json();
                    if (errorData.error) {
                        errorMessage = errorData.error;
                    } else if (errorData.message) {
                        errorMessage = errorData.message;
                    }
                } catch (e) {
                    // Use default message
                }
                this.showToast(errorMessage, 'error');
            }
        } catch (error) {
            console.error('Error accepting friend request:', error);
            let errorMessage = 'Error accepting friend request';
            if (error.message && error.message.includes('Unauthorized')) {
                errorMessage = 'You must be logged in to accept friend requests';
            } else if (error.message) {
                errorMessage = 'Error: ' + error.message;
            }
            this.showToast(errorMessage, 'error');
        }
    }

    async declineFriendRequest(friendshipId) {
        if (!this.currentUser) {
            this.showToast('Please log in to decline friend requests', 'error');
            return;
        }

        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/friendships/${friendshipId}/decline`, {
                method: 'DELETE'
            });

            if (response.ok) {
                this.showToast('Friend request declined', 'success');
                await this.loadFriendRequests();
            } else {
                // Try to get error message
                let errorMessage = 'Failed to decline friend request';
                try {
                    const errorData = await response.json();
                    if (errorData.error) {
                        errorMessage = errorData.error;
                    } else if (errorData.message) {
                        errorMessage = errorData.message;
                    }
                } catch (e) {
                    // Use default message
                }
                this.showToast(errorMessage, 'error');
            }
        } catch (error) {
            console.error('Error declining friend request:', error);
            let errorMessage = 'Error declining friend request';
            if (error.message && error.message.includes('Unauthorized')) {
                errorMessage = 'You must be logged in to decline friend requests';
            } else if (error.message) {
                errorMessage = 'Error: ' + error.message;
            }
            this.showToast(errorMessage, 'error');
        }
    }

    async loadCurrentFriends() {
        if (!this.currentUser) return;

        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/friendships/friends/${this.currentUser.userId}`);
            const friends = await response.json();
            this.displayCurrentFriends(friends);
        } catch (error) {
            console.error('Error loading friends:', error);
        }
    }

    displayCurrentFriends(friends) {
        const container = document.getElementById('current-friends');
        if (!container) return;

        if (friends.length === 0) {
            container.innerHTML = '<p>No friends yet. Start by searching for people!</p>';
            return;
        }

        container.innerHTML = friends.map(friendship => {
            const friend = friendship.user1.userId === this.currentUser.userId ? 
                          friendship.user2 : friendship.user1;
            return `
                <div class="user-card">
                    <div class="user-info">
                        <h4>${friend.firstName || ''} ${friend.lastName || ''}</h4>
                        <p>@${friend.username}</p>
                    </div>
                    <div class="user-actions">
                        <button class="btn btn-primary btn-small" onclick="app.startConversation(${friend.userId})">
                            Message
                        </button>
                        <button class="btn btn-secondary btn-small" onclick="app.removeFriend(${friendship.friendshipId}, '${friend.firstName || ''} ${friend.lastName || ''}')">
                            Remove
                        </button>
                    </div>
                </div>
            `;
        }).join('');
    }

    // Messages Functions
    async loadMessages() {
        if (!this.currentUser) return;
        await this.loadConversations();
    }

    async loadConversations() {
        try {
            // Get friends as conversations
            const response = await this.authFetch(`${this.apiBaseUrl}/friendships/friends/${this.currentUser.userId}`);
            const friends = await response.json();
            this.displayConversations(friends);
        } catch (error) {
            console.error('Error loading conversations:', error);
        }
    }

    displayConversations(conversations) {
        const container = document.getElementById('conversations-list');
        if (!container) return;

        if (conversations.length === 0) {
            container.innerHTML = '<p>No conversations yet. Add some friends to start messaging!</p>';
            return;
        }

        container.innerHTML = conversations.map(friendship => {
            const friend = friendship.user1.userId === this.currentUser.userId ? 
                          friendship.user2 : friendship.user1;
            return `
                <div class="conversation-item" onclick="app.openConversation(${friend.userId}, '${friend.firstName || ''} ${friend.lastName || ''}')">
                    <div class="conversation-user">${friend.firstName || ''} ${friend.lastName || ''}</div>
                    <div class="conversation-preview">@${friend.username}</div>
                </div>
            `;
        }).join('');
    }

    async openConversation(friendId, friendName) {
        console.log('Opening conversation with:', friendId, friendName);
        this.currentConversation = { id: friendId, name: friendName };
        
        // Update chat header
        const chatHeader = document.getElementById('chat-header');
        if (chatHeader) {
            chatHeader.innerHTML = `<h3>Chat with ${friendName}</h3>`;
        }
        
        // Show chat input
        const chatInputArea = document.getElementById('chat-input-area');
        if (chatInputArea) {
            chatInputArea.style.display = 'block';
            console.log('Chat input area shown');
        } else {
            console.error('Chat input area not found');
        }
        
        // Load conversation messages
        await this.loadConversationMessages(friendId);
    }

    async loadConversationMessages(friendId) {
        try {
            console.log('Loading conversation messages between', this.currentUser.userId, 'and', friendId);
            // Use the secure endpoint that requires the current user ID
            const response = await this.authFetch(`${this.apiBaseUrl}/messages/my-conversation/${this.currentUser.userId}/${friendId}`);
            if (!response.ok) {
                console.error('Failed to fetch messages:', response.status);
                this.displayMessages([]);
                return;
            }
            const messages = await response.json();
            console.log('Messages received:', messages);
            this.displayMessages(messages);
        } catch (error) {
            console.error('Error loading messages:', error);
            this.displayMessages([]);
        }
    }

    displayMessages(messages) {
        const container = document.getElementById('chat-messages');
        if (!container) return;

        if (messages.length === 0) {
            container.innerHTML = '<p>No messages yet. Start the conversation!</p>';
            return;
        }

        container.innerHTML = messages.map(message => `
            <div class="message ${message.senderId === this.currentUser.userId ? 'sent' : 'received'}">
                <div class="message-bubble">${message.message}</div>
                <div class="message-time">${this.formatDate(message.data)}</div>
            </div>
        `).join('');
    }

    async handleSendMessage(e) {
        e.preventDefault();
        console.log('Send message form submitted');
        
        if (!this.currentConversation) {
            this.showToast('Please select a conversation first', 'error');
            return;
        }

        const messageInput = document.getElementById('message-input');
        const content = messageInput.value.trim();
        
        if (!content) {
            this.showToast('Please enter a message', 'error');
            return;
        }

        console.log('Debug - Current user:', this.currentUser);
        console.log('Debug - Current conversation:', this.currentConversation);
        console.log('Debug - Content:', content);
        console.log('Debug - User ID:', this.currentUser?.userId);
        console.log('Debug - Conversation ID:', this.currentConversation?.id);
        console.log('Debug - User keys:', this.currentUser ? Object.keys(this.currentUser) : 'No user');
        console.log('Debug - Conversation keys:', this.currentConversation ? Object.keys(this.currentConversation) : 'No conversation');
        
        // Validate required data
        if (!this.currentUser || !this.currentUser.userId) {
            this.showToast('User not logged in properly', 'error');
            return;
        }
        
        if (!this.currentConversation || !this.currentConversation.id) {
            this.showToast('No conversation selected', 'error');
            return;
        }

        console.log('Sending message:', {
            message: content,
            senderUserId: this.currentUser.userId,
            recipientUserId: this.currentConversation.id
        });

        const requestBody = {
            message: content,
            senderUserId: this.currentUser.userId,
            recipientUserId: this.currentConversation.id
        };
        
        console.log('Request body being sent:', requestBody);
        console.log('JSON stringified:', JSON.stringify(requestBody));
        
        // First test the raw endpoint
        try {
            console.log('Testing raw endpoint...');
            const rawResponse = await this.authFetch(`${this.apiBaseUrl}/messages/raw`, {
                method: 'POST',
                body: JSON.stringify(requestBody)
            });
            
            if (rawResponse.ok) {
                const rawResult = await rawResponse.text();
                console.log('Raw endpoint response:', rawResult);
            } else {
                console.error('Raw endpoint failed:', rawResponse.status);
            }
        } catch (rawError) {
            console.error('Raw endpoint error:', rawError);
        }
        
        // Now try the actual endpoint
        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/messages`, {
                method: 'POST',
                body: JSON.stringify(requestBody)
            });

            if (response.ok) {
                const result = await response.json();
                console.log('Message sent successfully:', result);
                messageInput.value = '';
                this.loadConversationMessages(this.currentConversation.id);
            } else {
                const errorText = await response.text();
                console.error('Failed to send message:', response.status, errorText);
                this.showToast('Failed to send message: ' + errorText, 'error');
            }
        } catch (error) {
            console.error('Error sending message:', error);
            this.showToast('Error sending message', 'error');
        }
    }

    async startConversation(friendId) {
        // Switch to messages page and open conversation
        this.showPage('messages-page');
        setTimeout(() => {
            this.openConversation(friendId, 'Friend');
        }, 100);
    }

    async removeFriend(friendshipId, friendName) {
        if (!confirm(`Are you sure you want to remove ${friendName} from your friends?`)) {
            return;
        }

        try {
            console.log('Removing friend with friendship ID:', friendshipId);
            const response = await this.authFetch(`${this.apiBaseUrl}/friendships/${friendshipId}/remove`, {
                method: 'DELETE'
            });

            if (response.ok) {
                this.showToast(`Successfully removed ${friendName} from friends`, 'success');
                // Refresh the friends list
                this.loadCurrentFriends();
            } else {
                const errorText = await response.text();
                console.error('Failed to remove friend:', response.status, errorText);
                this.showToast('Failed to remove friend: ' + errorText, 'error');
            }
        } catch (error) {
            console.error('Error removing friend:', error);
            this.showToast('Error removing friend', 'error');
        }
    }

    // Post Actions
    async toggleLike(postId) {
        if (!this.currentUser) {
            this.showToast('Please log in to like posts', 'error');
            return;
        }

        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/posts/${postId}/toggle-like?userId=${this.currentUser.userId}`, {
                method: 'POST'
            });

            if (response.ok) {
                const result = await response.json();
                this.updateLikeButton(postId, result.isLiked, result.likeCount);
                
                if (result.isLiked) {
                    this.showToast('Post liked!', 'success');
                } else {
                    this.showToast('Post unliked!', 'success');
                }
            } else {
                this.showToast('Failed to toggle like', 'error');
            }
        } catch (error) {
            console.error('Error toggling like:', error);
            this.showToast('Error toggling like', 'error');
        }
    }
    
    updateLikeButton(postId, isLiked, likeCount) {
        const likeBtn = document.getElementById(`like-btn-${postId}`);
        const likeText = likeBtn.querySelector('.like-text');
        const likeIcon = likeBtn.querySelector('i');
        
        if (isLiked) {
            likeBtn.classList.add('liked');
            likeText.textContent = 'Unlike';
            likeIcon.style.color = '#e74c3c';
        } else {
            likeBtn.classList.remove('liked');
            likeText.textContent = 'Like';
            likeIcon.style.color = '';
        }
        
        // Update like count display
        const likeCountSpan = likeBtn.closest('.post-item').querySelector('.likes-count');
        likeCountSpan.textContent = `${likeCount} likes`;
    }
    
    async loadLikeStatus(postId) {
        if (!this.currentUser) return;
        
        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/posts/${postId}/like-status?userId=${this.currentUser.userId}`);
            if (response.ok) {
                const result = await response.json();
                this.updateLikeButton(postId, result.hasLiked, result.likeCount);
            }
        } catch (error) {
            console.error('Error loading like status:', error);
        }
    }

    toggleComments(postId) {
        const commentsDiv = document.getElementById(`comments-${postId}`);
        if (commentsDiv) {
            commentsDiv.style.display = commentsDiv.style.display === 'none' ? 'block' : 'none';
            
            // Add word count listener when comments are shown
            if (commentsDiv.style.display === 'block') {
                this.addWordCountListener(postId);
            }
        }
    }
    
    addWordCountListener(postId) {
        const input = document.getElementById(`comment-input-${postId}`);
        const wordCount = document.getElementById(`word-count-${postId}`);
        
        if (input && wordCount) {
            input.addEventListener('input', () => {
                const charCount = input.value.length;
                wordCount.textContent = `${charCount}/70 characters`;
                
                if (charCount > 70) {
                    wordCount.style.color = '#e74c3c';
                    input.style.borderColor = '#e74c3c';
                } else {
                    wordCount.style.color = '#666';
                    input.style.borderColor = '';
                }
            });
        }
    }
    
    async addComment(postId) {
        if (!this.currentUser) {
            this.showToast('Please log in to comment', 'error');
            return;
        }

        const commentInput = document.getElementById(`comment-input-${postId}`);
        const content = commentInput.value.trim();
        
        if (!content) {
            this.showToast('Please enter a comment', 'error');
            return;
        }
        
        // Check character count
        if (content.length > 70) {
            this.showToast('Comment exceeds 70 characters limit', 'error');
            return;
        }

        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/comments`, {
                method: 'POST',
                body: JSON.stringify({
                    content: content,
                    postId: postId,
                    userId: this.currentUser.userId
                })
            });

            if (response.ok) {
                const newComment = await response.json();
                this.showToast('Comment added!', 'success');
                commentInput.value = '';
                document.getElementById(`word-count-${postId}`).textContent = '0/70 characters';
                this.loadComments(postId);
            } else {
                this.showToast('Failed to add comment', 'error');
            }
        } catch (error) {
            console.error('Error adding comment:', error);
            this.showToast('Error adding comment', 'error');
        }
    }
    
    async loadComments(postId) {
        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/comments/post/${postId}`);
            if (response.ok) {
                const comments = await response.json();
                this.displayComments(postId, comments);
            }
        } catch (error) {
            console.error('Error loading comments:', error);
        }
    }
    
    displayComments(postId, comments) {
        const commentsList = document.getElementById(`comments-list-${postId}`);
        if (commentsList) {
            commentsList.innerHTML = comments.map(comment => `
                <div class="comment-item">
                    <strong>${comment.username}:</strong> ${comment.content}
                    ${comment.userId === this.currentUser?.userId ? `<button class="delete-comment-btn" onclick="app.deleteComment(${comment.commentId})">×</button>` : ''}
                </div>
            `).join('');
        }
    }
    
    async deleteComment(commentId) {
        if (!this.currentUser) {
            this.showToast('Please log in to delete comments', 'error');
            return;
        }

        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/comments/${commentId}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                this.showToast('Comment deleted!', 'success');
                // Reload all comments for all posts
                document.querySelectorAll('[id^="comments-"]').forEach(commentsDiv => {
                    const postId = commentsDiv.id.replace('comments-', '');
                    this.loadComments(parseInt(postId));
                });
            } else {
                this.showToast('Failed to delete comment', 'error');
            }
        } catch (error) {
            console.error('Error deleting comment:', error);
            this.showToast('Error deleting comment', 'error');
        }
    }

    // Admin Dashboard Functions
    async loadAdminDashboard() {
        if (!this.currentUser || this.currentUser.role !== 'ADMIN') {
            this.showToast('Access denied. Admin privileges required.', 'error');
            return;
        }

        try {
            // Load statistics
            await Promise.all([
                this.loadAdminStats(),
                this.loadAllUsers(),
                this.loadAllPosts()
            ]);
            
            // Update last updated time
            const lastUpdated = document.getElementById('last-updated');
            if (lastUpdated) {
                lastUpdated.textContent = new Date().toLocaleString();
            }
        } catch (error) {
            console.error('Error loading admin dashboard:', error);
            this.showToast('Error loading admin dashboard', 'error');
        }
    }

    async loadAdminStats() {
        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/admin/stats`);
            if (!response.ok) {
                throw new Error('Failed to load statistics');
            }
            const stats = await response.json();
            
            document.getElementById('total-users').textContent = stats.totalUsers || 0;
            document.getElementById('total-posts').textContent = stats.totalPosts || 0;
            document.getElementById('total-comments').textContent = stats.totalComments || 0;
            document.getElementById('total-likes').textContent = stats.totalLikes || 0;
        } catch (error) {
            console.error('Error loading admin stats:', error);
        }
    }

    async loadAllUsers() {
        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/admin/users`);
            if (!response.ok) {
                throw new Error('Failed to load users');
            }
            const users = await response.json();
            this.displayUsers(users);
        } catch (error) {
            console.error('Error loading users:', error);
            this.showToast('Error loading users', 'error');
        }
    }

    displayUsers(users) {
        const container = document.getElementById('users-list');
        if (!container) return;

        if (users.length === 0) {
            container.innerHTML = '<p>No users found.</p>';
            return;
        }

        container.innerHTML = users.map(user => `
            <div class="user-card">
                <div class="user-info">
                    <h4>${user.firstName || ''} ${user.lastName || ''}</h4>
                    <p>@${user.username}</p>
                    <p>${user.email}</p>
                    <p>Role: ${user.role || 'USER'}</p>
                </div>
                <div class="user-actions">
                    <button class="btn btn-secondary btn-small" onclick="app.deleteUserAsAdmin(${user.userId})">
                        Delete
                    </button>
                </div>
            </div>
        `).join('');
    }

    async searchUsers() {
        const searchTerm = document.getElementById('user-search')?.value.trim();
        if (!searchTerm) {
            await this.loadAllUsers();
            return;
        }

        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/admin/users/search?term=${encodeURIComponent(searchTerm)}`);
            if (!response.ok) {
                throw new Error('Failed to search users');
            }
            const users = await response.json();
            this.displayUsers(users);
        } catch (error) {
            console.error('Error searching users:', error);
            this.showToast('Error searching users', 'error');
        }
    }

    async deleteUserAsAdmin(userId) {
        if (!confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
            return;
        }

        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/admin/users/${userId}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                this.showToast('User deleted successfully', 'success');
                await this.loadAllUsers();
                await this.loadAdminStats();
            } else {
                this.showToast('Failed to delete user', 'error');
            }
        } catch (error) {
            console.error('Error deleting user:', error);
            this.showToast('Error deleting user', 'error');
        }
    }

    async loadAllPosts() {
        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/admin/posts`);
            if (!response.ok) {
                throw new Error('Failed to load posts');
            }
            const posts = await response.json();
            this.displayAdminPosts(posts);
        } catch (error) {
            console.error('Error loading posts:', error);
            this.showToast('Error loading posts', 'error');
        }
    }

    displayAdminPosts(posts) {
        const container = document.getElementById('admin-posts-list');
        if (!container) return;

        if (posts.length === 0) {
            container.innerHTML = '<p>No posts found.</p>';
            return;
        }

        container.innerHTML = posts.map(post => `
            <div class="post-item">
                <div class="post-header">
                    <span class="post-author">${post.username || 'Unknown User'}</span>
                    <span class="post-date">${this.formatDate(post.createdAt)}</span>
                </div>
                <div class="post-content">${post.content}</div>
                <div class="post-stats">
                    <span>${post.likesCount || 0} likes</span>
                    <span>${post.comments ? post.comments.length : 0} comments</span>
                </div>
                <div class="post-actions">
                    <button class="btn btn-secondary btn-small" onclick="app.deletePostAsAdmin(${post.postId})">
                        Delete Post
                    </button>
                </div>
            </div>
        `).join('');
    }

    async deletePostAsAdmin(postId) {
        if (!confirm('Are you sure you want to delete this post?')) {
            return;
        }

        try {
            const response = await this.authFetch(`${this.apiBaseUrl}/admin/posts/${postId}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                this.showToast('Post deleted successfully', 'success');
                await this.loadAllPosts();
                await this.loadAdminStats();
            } else {
                this.showToast('Failed to delete post', 'error');
            }
        } catch (error) {
            console.error('Error deleting post:', error);
            this.showToast('Error deleting post', 'error');
        }
    }

}

// Initialize the application
const app = new FacebookLiteApp();

// Global functions for HTML onclick handlers
function showLogin() {
    app.showLogin();
}

function showSignUp() {
    app.showSignUp();
}

function logout() {
    app.logout();
}
