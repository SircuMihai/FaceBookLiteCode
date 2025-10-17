// FacebookLite Frontend Application
class FacebookLiteApp {
    constructor() {
        this.apiBaseUrl = 'http://localhost:8082/api';
        this.currentUser = null;
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.checkAuthStatus();
        this.showPage('home-page');
    }

    setupEventListeners() {
        // Navigation toggle for mobile
        const navToggle = document.getElementById('nav-toggle');
        const navMenu = document.getElementById('nav-menu');
        
        if (navToggle) {
            navToggle.addEventListener('click', () => {
                navMenu.classList.toggle('active');
            });
        }

        // Login form
        const loginForm = document.getElementById('login-form');
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }

        // Sign up form
        const signupForm = document.getElementById('signup-form');
        if (signupForm) {
            signupForm.addEventListener('submit', (e) => this.handleSignUp(e));
        }

        // Post form
        const postForm = document.getElementById('post-form');
        if (postForm) {
            postForm.addEventListener('submit', (e) => this.handleCreatePost(e));
        }

        // Edit profile form
        const editProfileForm = document.getElementById('edit-profile-form');
        if (editProfileForm) {
            editProfileForm.addEventListener('submit', (e) => this.handleEditProfile(e));
        }

        // Message form
        const messageForm = document.getElementById('message-form');
        if (messageForm) {
            messageForm.addEventListener('submit', (e) => this.handleSendMessage(e));
        }
    }

    // Navigation Functions
    showPage(pageId) {
        // Hide all pages
        const pages = document.querySelectorAll('.page');
        pages.forEach(page => page.classList.remove('active'));

        // Show selected page
        const targetPage = document.getElementById(pageId);
        if (targetPage) {
            targetPage.classList.add('active');
        }

        // Update navigation
        this.updateNavigation();

        // Load page-specific content
        if (pageId === 'dashboard-page' && this.currentUser) {
            this.loadDashboard();
        } else if (pageId === 'profile-page' && this.currentUser) {
            this.loadProfile();
        } else if (pageId === 'friends-page' && this.currentUser) {
            this.loadFriends();
        } else if (pageId === 'messages-page' && this.currentUser) {
            this.loadMessages();
        }
    }

    updateNavigation() {
        const navLinks = document.getElementById('nav-links');
        const navAuth = document.getElementById('nav-auth');

        if (this.currentUser) {
            // User is logged in
            navLinks.innerHTML = `
                <a href="#" onclick="app.showPage('dashboard-page')">Dashboard</a>
                <a href="#" onclick="app.showPage('dashboard-page')">Posts</a>
            `;
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

        try {
            // Get all users and find matching username
            const response = await fetch(`${this.apiBaseUrl}/users`);
            const users = await response.json();
            
            const user = users.find(u => u.username === loginData.username);
            
            if (user) {
                this.currentUser = user;
                localStorage.setItem('currentUser', JSON.stringify(user));
                this.showToast('Login successful!', 'success');
                this.showPage('dashboard-page');
            } else {
                this.showToast('Invalid username or password', 'error');
            }
        } catch (error) {
            console.error('Login error:', error);
            this.showToast('Login failed. Please try again.', 'error');
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
            lastName: formData.get('lastName'),
            role: 'USER',
            privateAccount: false
        };

        try {
            const response = await fetch(`${this.apiBaseUrl}/users`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData)
            });

            if (response.ok) {
                const newUser = await response.json();
                this.currentUser = newUser;
                localStorage.setItem('currentUser', JSON.stringify(newUser));
                this.showToast('Account created successfully!', 'success');
                this.showPage('dashboard-page');
            } else {
                const error = await response.text();
                this.showToast('Registration failed: ' + error, 'error');
            }
        } catch (error) {
            console.error('Registration error:', error);
            this.showToast('Registration failed. Please try again.', 'error');
        } finally {
            this.showLoading(false);
        }
    }

    logout() {
        this.currentUser = null;
        localStorage.removeItem('currentUser');
        this.showToast('Logged out successfully', 'success');
        this.showPage('home-page');
    }

    checkAuthStatus() {
        const savedUser = localStorage.getItem('currentUser');
        if (savedUser) {
            this.currentUser = JSON.parse(savedUser);
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
            const response = await fetch(`${this.apiBaseUrl}/posts/user/${this.currentUser.userId}`);
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
            const response = await fetch(`${this.apiBaseUrl}/posts`);
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
            const response = await fetch(`${this.apiBaseUrl}/simple/create-post`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
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
            const response = await fetch(`${this.apiBaseUrl}/users/${this.currentUser.userId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(updateData)
            });

            if (response.ok) {
                const updatedUser = await response.json();
                this.currentUser = updatedUser;
                localStorage.setItem('currentUser', JSON.stringify(updatedUser));
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
                fetch(`${this.apiBaseUrl}/users`),
                fetch(`${this.apiBaseUrl}/friendships/friends/${this.currentUser.userId}`)
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
                fetch(`${this.apiBaseUrl}/users`),
                fetch(`${this.apiBaseUrl}/friendships/friends/${this.currentUser.userId}`)
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
            const response = await fetch(`${this.apiBaseUrl}/friendships/friend-request`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
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
            const response = await fetch(`${this.apiBaseUrl}/friendships/requests/${this.currentUser.userId}`);
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
            const friendshipId = request.friendshipId || request.id;
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
        }).join('');
    }

    async acceptFriendRequest(friendshipId) {
        try {
            const response = await fetch(`${this.apiBaseUrl}/friendships/${friendshipId}/accept`, {
                method: 'PUT'
            });

            if (response.ok) {
                this.showToast('Friend request accepted!', 'success');
                this.loadFriendRequests();
                this.loadCurrentFriends();
            } else {
                this.showToast('Failed to accept friend request', 'error');
            }
        } catch (error) {
            console.error('Error accepting friend request:', error);
            this.showToast('Error accepting friend request', 'error');
        }
    }

    async declineFriendRequest(friendshipId) {
        try {
            const response = await fetch(`${this.apiBaseUrl}/friendships/${friendshipId}/decline`, {
                method: 'DELETE'
            });

            if (response.ok) {
                this.showToast('Friend request declined', 'success');
                this.loadFriendRequests();
            } else {
                this.showToast('Failed to decline friend request', 'error');
            }
        } catch (error) {
            console.error('Error declining friend request:', error);
            this.showToast('Error declining friend request', 'error');
        }
    }

    async loadCurrentFriends() {
        if (!this.currentUser) return;

        try {
            const response = await fetch(`${this.apiBaseUrl}/friendships/friends/${this.currentUser.userId}`);
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
            const response = await fetch(`${this.apiBaseUrl}/friendships/friends/${this.currentUser.userId}`);
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
            const response = await fetch(`${this.apiBaseUrl}/messages/my-conversation/${this.currentUser.userId}/${friendId}`);
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
            const rawResponse = await fetch(`${this.apiBaseUrl}/messages/raw`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
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
            const response = await fetch(`${this.apiBaseUrl}/messages`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
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
            const response = await fetch(`${this.apiBaseUrl}/friendships/${friendshipId}/remove`, {
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
            const response = await fetch(`${this.apiBaseUrl}/posts/${postId}/toggle-like?userId=${this.currentUser.userId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
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
            const response = await fetch(`${this.apiBaseUrl}/posts/${postId}/like-status?userId=${this.currentUser.userId}`);
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
            const response = await fetch(`${this.apiBaseUrl}/comments`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
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
            const response = await fetch(`${this.apiBaseUrl}/comments/post/${postId}`);
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
            const response = await fetch(`${this.apiBaseUrl}/comments/${commentId}`, {
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
