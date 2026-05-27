const { useState, useEffect, useRef } = React;

class ErrorBoundary extends React.Component {
    constructor(props) {
        super(props);
        this.state = { hasError: false, message: '' };
    }
    static getDerivedStateFromError(error) {
        return { hasError: true, message: error.toString() };
    }
    render() {
        if (this.state.hasError) {
            return <div style={{padding:'40px', color:'#ff7b72', background:'#010409'}}><h2>React Component Crashed</h2><pre>{this.state.message}</pre></div>;
        }
        return this.props.children;
    }
}

function App() {
    const [token, setToken] = useState(localStorage.getItem('jwtToken'));

    const handleLogin = (newToken) => {
        localStorage.setItem('jwtToken', newToken);
        setToken(newToken);
    };

    const handleLogout = () => {
        localStorage.removeItem('jwtToken');
        setToken(null);
    };

    return (
        <ErrorBoundary>
            <div>
                {token ? (
                    <Dashboard token={token} onLogout={handleLogout} />
                ) : (
                    <Auth onLogin={handleLogin} />
                )}
            </div>
        </ErrorBoundary>
    );
}

function Auth({ onLogin }) {
    const [isLoginMode, setIsLoginMode] = useState(true);
    const [formData, setFormData] = useState({ displayName: '', username: '', email: '', password: '' });
    const [error, setError] = useState('');

    const validatePassword = (value) => {
        if (value.length < 8) return 'Password must be at least 8 characters.';
        if (!/[a-z]/.test(value)) return 'Password must include a lowercase letter.';
        if (!/[A-Z]/.test(value)) return 'Password must include an uppercase letter.';
        if (!/\d/.test(value)) return 'Password must include a number.';
        if (!/[^A-Za-z0-9]/.test(value)) return 'Password must include a special character.';
        return '';
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!isLoginMode) {
            const passwordError = validatePassword(formData.password);
            if (passwordError) {
                setError(passwordError);
                return;
            }
        }
        
        const endpoint = isLoginMode ? '/api/v1/auth/authenticate' : '/api/v1/auth/register';
        const payload = isLoginMode 
            ? { username: formData.username.trim(), password: formData.password }
            : {
                username: (formData.username || formData.email).trim(),
                email: formData.email.trim(),
                password: formData.password,
                displayName: (formData.displayName || formData.username || formData.email).trim(),
                role: 'STUDENT'
            };

        try {
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const body = await response.json().catch(() => ({}));
                throw new Error(body.message || (isLoginMode ? 'Invalid username or password.' : 'Registration failed.'));
            }

            const data = await response.json();
            onLogin(data.token);
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div className="card auth-container">
            <h2>{isLoginMode ? 'Sign In to CodeTracker' : 'Create an Account'}</h2>
            {error && <div className="error-msg">{error}</div>}
            <form onSubmit={handleSubmit}>
                {!isLoginMode && (
                    <div className="form-group">
                        <label>Full Name</label>
                        <input type="text" value={formData.displayName} onChange={e => setFormData({...formData, displayName: e.target.value})} required />
                    </div>
                )}
                <div className="form-group">
                    <label>{isLoginMode ? 'Email or Username' : 'App Username'}</label>
                    <input type="text" value={formData.username} onChange={e => setFormData({...formData, username: e.target.value})} required />
                </div>
                {!isLoginMode && (
                    <div className="form-group">
                        <label>Email</label>
                        <input type="email" value={formData.email} onChange={e => setFormData({...formData, email: e.target.value})} required />
                    </div>
                )}
                <div className="form-group">
                    <label>Password</label>
                    <input
                        type="password"
                        value={formData.password}
                        onChange={e => setFormData({...formData, password: e.target.value})}
                        minLength={isLoginMode ? undefined : 8}
                        pattern={isLoginMode ? undefined : "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}"}
                        title="At least 8 characters with uppercase, lowercase, number, and special character"
                        required
                    />
                    {!isLoginMode && <p style={{fontSize: '0.8rem', color: '#8b949e'}}>Use 8+ chars with uppercase, lowercase, number, and special character.</p>}
                </div>
                <button type="submit" className="btn">
                    {isLoginMode ? 'Login' : 'Sign Up'}
                </button>
            </form>
            <a className="toggle-link" onClick={() => setIsLoginMode(!isLoginMode)}>
                {isLoginMode ? "Don't have an account? Sign up" : "Already have an account? Login"}
            </a>
        </div>
    );
}

// Reusable Chart Component using Chart.js with safe-check
function ChartView({ type, data, options }) {
    const chartRef = useRef(null);
    const chartInstance = useRef(null);
    const [chartError, setChartError] = useState(false);

    useEffect(() => {
        if (typeof Chart === 'undefined') {
            console.error("Chart.js failed to load from CDN!");
            setChartError(true);
            return;
        }

        if (chartInstance.current) {
            chartInstance.current.destroy();
        }
        if (chartRef.current) {
            try {
                const ctx = chartRef.current.getContext('2d');
                chartInstance.current = new Chart(ctx, { type, data, options });
            } catch (e) {
                console.error("Failed to render Chart", e);
                setChartError(true);
            }
        }
        return () => {
            if (chartInstance.current) chartInstance.current.destroy();
        };
    }, [data, type, options]);

    if (chartError) return <div style={{color:'#8b949e', fontStyle:'italic', marginTop:'40px'}}>Failed to load Graph API...</div>;
    return <canvas ref={chartRef}></canvas>;
}

function Dashboard({ token, onLogout }) {
    // Platform Usernames
    const [githubUsername, setGithubUsername] = useState('octocat');
    const [leetcodeUsername, setLeetcodeUsername] = useState('');
    const [gfgUsername, setGfgUsername] = useState('');
    const [hackerrankUsername, setHackerrankUsername] = useState('');

    // Fetched Data
    const [githubData, setGithubData] = useState([]);
    const [leetcodeData, setLeetcodeData] = useState([]);
    const [leetcodeStats, setLeetcodeStats] = useState(null);
    const [gfgStats, setGfgStats] = useState(null);
    const [hackerrankStats, setHackerrankStats] = useState(null);

    const [loading, setLoading] = useState('');

    const fetchGithubEvents = async (uname) => {
        if(!uname) return;
        try {
            const res = await fetch(`/api/v1/integrations/github/${uname}`, { headers: { 'Authorization': `Bearer ${token}` } });
            if(res.ok) setGithubData(await res.json());
        } catch(e) { console.error(e); }
    };

    const fetchLeetcodeData = async (uname) => {
        if(!uname) return;
        try {
            const resList = await fetch(`/api/v1/integrations/leetcode/${uname}`, { headers: { 'Authorization': `Bearer ${token}` } });
            if(resList.ok) setLeetcodeData(await resList.json());
            
            const resStats = await fetch(`/api/v1/integrations/leetcode/${uname}/stats`, { headers: { 'Authorization': `Bearer ${token}` } });
            if(resStats.ok) {
                const s = await resStats.json();
                if(s && s.submitStats && s.submitStats.acSubmissionNum) {
                    setLeetcodeStats(s.submitStats.acSubmissionNum);
                }
            }
        } catch(e) { console.error(e); }
    };

    const fetchGeeksForGeeksData = async (uname) => {
        if(!uname) return;
        try {
            const res = await fetch(`/api/v1/integrations/geeksforgeeks/${uname}/stats`, { headers: { 'Authorization': `Bearer ${token}` } });
            if(res.ok) setGfgStats(await res.json());
        } catch(e) { console.error(e); }
    };

    const fetchHackerRankData = async (uname) => {
        if(!uname) return;
        try {
            const res = await fetch(`/api/v1/integrations/hackerrank/${uname}/stats`, { headers: { 'Authorization': `Bearer ${token}` } });
            if(res.ok) setHackerrankStats(await res.json());
        } catch(e) { console.error(e); }
    };

    useEffect(() => {
        fetchGithubEvents(githubUsername);
    }, []);

    const handleSync = async (e, platform) => {
        if(e) e.preventDefault();
        setLoading(platform);
        if(platform === 'github') await fetchGithubEvents(githubUsername);
        if(platform === 'leetcode') await fetchLeetcodeData(leetcodeUsername);
        if(platform === 'geeksforgeeks') await fetchGeeksForGeeksData(gfgUsername);
        if(platform === 'hackerrank') await fetchHackerRankData(hackerrankUsername);
        setLoading('');
    };

    // Calculate Charts Data
    
    // LeetCode Stats Chart Data
    let lcInsights = [];
    let lcChartData = null;
    let lcTotalSolved = 0;
    if (leetcodeStats && Array.isArray(leetcodeStats) && leetcodeStats.length > 0) {
        const easy = leetcodeStats.find(s => s.difficulty === 'Easy')?.count || 0;
        const medium = leetcodeStats.find(s => s.difficulty === 'Medium')?.count || 0;
        const hard = leetcodeStats.find(s => s.difficulty === 'Hard')?.count || 0;
        lcTotalSolved = leetcodeStats.find(s => s.difficulty === 'All')?.count || (easy+medium+hard);
        
        lcChartData = {
            labels: ['Easy', 'Medium', 'Hard'],
            datasets: [{
                data: [easy, medium, hard],
                backgroundColor: ['#00b8a3', '#ffc01e', '#ff375f'],
                borderWidth: 0
            }]
        };

        if (easy > 0 && medium === 0) lcInsights.push("Good start! Try stepping out of your comfort zone with a few Medium problems.");
        else if (medium > easy) lcInsights.push("Great work! You're consistently taking on challenging Medium problems.");
        else if (easy > 0) lcInsights.push("You have a well-balanced foundation across varying difficulties.");
        if (hard >= 10) lcInsights.push("Amazing! You have solid experience tackling Hard algorithmic challenges.");
        else if (medium > 20 && hard === 0) lcInsights.push("You are mastering Mediums. It's time to attempt your first Hard problems!");
    }

    const gfgSolved = gfgStats?.solved || 0;
    const gfgChartData = gfgStats ? {
        labels: ['Solved', 'Practice Target'],
        datasets: [{
            data: [gfgSolved, Math.max(0, 100 - gfgSolved)],
            backgroundColor: ['#16a34a', '#30363d'],
            borderWidth: 0
        }]
    } : null;

    const hackerrankSolved = hackerrankStats?.solved || 0;
    const hackerrankChartData = hackerrankStats ? {
        labels: ['Solved', 'Practice Target'],
        datasets: [{
            data: [hackerrankSolved, Math.max(0, 100 - hackerrankSolved)],
            backgroundColor: ['#10b981', '#30363d'],
            borderWidth: 0
        }]
    } : null;

    const darkDoughnutOptions = {
        plugins: { legend: { labels: { color: 'white' } } },
        cutout: '70%'
    };

    return (
        <div>
            <div className="nav-bar">
                <h1>CodeTracker Dashboard</h1>
                <button className="btn logout-btn" onClick={onLogout}>Logout</button>
            </div>
            
            <div className="dashboard-grid">
                
                {/* LeetCode Integration Panel */}
                <div className="card">
                    <h2>LeetCode</h2>
                    <form onSubmit={(e) => handleSync(e, 'leetcode')} style={{display: 'flex', gap: '8px', marginBottom: '1rem', width: '100%'}}>
                        <input style={{flex: 1, padding:'0.5rem', borderRadius: '4px', border: '1px solid var(--border-color)', background: '#0d1117', color: 'white'}} type="text" value={leetcodeUsername} onChange={e => setLeetcodeUsername(e.target.value)} placeholder="LeetCode Username" required/>
                        <button type="submit" className="btn" style={{width: '80px'}} disabled={loading==='leetcode'}>{loading === 'leetcode' ? '...' : 'Sync'}</button>
                    </form>

                    {(!leetcodeData || leetcodeData.length === 0) && <p style={{color: '#8b949e'}}>Search a username to load LeetCode data.</p>}
                    
                    {lcChartData && (
                        <div style={{marginBottom: '1rem', padding: '1rem', background: '#0d1117', borderRadius: '8px'}}>
                            <h3 style={{margin: '0 0 10px 0', textAlign:'center'}}>Total Solved: {lcTotalSolved}</h3>
                            <div style={{height: '200px', display: 'flex', justifyContent: 'center'}}>
                                <ChartView type="doughnut" data={lcChartData} options={darkDoughnutOptions} />
                            </div>
                            <div style={{marginTop: '1rem', padding: '0.8rem', backgroundColor: '#161b22', borderRadius: '6px', fontSize: '0.9rem', color: '#c9d1d9'}}>
                                <strong style={{color: 'var(--primary-color)'}}>💡 Insights:</strong>
                                <ul style={{marginTop: '5px', paddingLeft: '20px', marginBottom: '0'}}>
                                    {lcInsights.map((ins, i) => <li key={i}>{ins}</li>)}
                                </ul>
                            </div>
                        </div>
                    )}

                    <div style={{ maxHeight: '250px', overflowY: 'auto', paddingRight: '5px' }}>
                    {(Array.isArray(leetcodeData) ? leetcodeData : []).map((prob, idx) => (
                        <div key={idx} className="integration-item">
                            <div style={{display: 'flex', justifyContent: 'space-between'}}>
                                <strong>{prob?.title || 'Unknown Title'}</strong>
                                <span style={{fontSize: '0.8rem', background: '#30363d', padding: '2px 6px', borderRadius: '10px'}}>Ac. Submission</span>
                            </div>
                            <div style={{fontSize: '0.85rem', color: '#8b949e'}}>Date: {prob?.timestamp ? new Date(parseInt(prob.timestamp) * 1000).toLocaleDateString() : 'N/A'}</div>
                        </div>
                    ))}
                    </div>
                </div>

                {/* GeeksforGeeks Integration Panel */}
                <div className="card">
                    <h2>GeeksforGeeks</h2>
                    <form onSubmit={(e) => handleSync(e, 'geeksforgeeks')} style={{display: 'flex', gap: '8px', marginBottom: '1rem', width: '100%'}}>
                        <input style={{flex: 1, padding:'0.5rem', borderRadius: '4px', border: '1px solid var(--border-color)', background: '#0d1117', color: 'white'}} type="text" value={gfgUsername} onChange={e => setGfgUsername(e.target.value)} placeholder="GeeksforGeeks Username" required/>
                        <button type="submit" className="btn" style={{width: '80px'}} disabled={loading==='geeksforgeeks'}>{loading === 'geeksforgeeks' ? '...' : 'Sync'}</button>
                    </form>

                    {!gfgStats && <p style={{color: '#8b949e'}}>Search a username to load GeeksforGeeks data.</p>}
                    
                    {gfgChartData && (
                        <div style={{marginBottom: '1rem', padding: '1rem', background: '#0d1117', borderRadius: '8px'}}>
                            <h3 style={{margin: '0 0 10px 0', textAlign: 'center'}}>Total Solved: {gfgSolved}</h3>
                            <div style={{height: '220px', display: 'flex', justifyContent: 'center'}}>
                                <ChartView type="doughnut" data={gfgChartData} options={darkDoughnutOptions} />
                            </div>
                            <div style={{marginTop: '1rem', padding: '0.8rem', backgroundColor: '#161b22', borderRadius: '6px', fontSize: '0.9rem', color: '#c9d1d9'}}>
                                <strong style={{color: 'var(--primary-color)'}}>💡 Insights:</strong>
                                <ul style={{marginTop: '5px', paddingLeft: '20px', listStyleType: 'none', marginLeft: '-20px', marginBottom: '0'}}>
                                    <li style={{marginBottom: '4px'}}>Coding Score: {gfgStats.codingScore || 'N/A'}</li>
                                    <li style={{marginBottom: '4px'}}>Institute Rank: {gfgStats.instituteRank || 'N/A'}</li>
                                </ul>
                            </div>
                        </div>
                    )}
                </div>

                {/* HackerRank Integration Panel */}
                <div className="card">
                    <h2>HackerRank</h2>
                    <form onSubmit={(e) => handleSync(e, 'hackerrank')} style={{display: 'flex', gap: '8px', marginBottom: '1rem', width: '100%'}}>
                        <input style={{flex: 1, padding:'0.5rem', borderRadius: '4px', border: '1px solid var(--border-color)', background: '#0d1117', color: 'white'}} type="text" value={hackerrankUsername} onChange={e => setHackerrankUsername(e.target.value)} placeholder="HackerRank Username" required/>
                        <button type="submit" className="btn" style={{width: '80px'}} disabled={loading==='hackerrank'}>{loading === 'hackerrank' ? '...' : 'Sync'}</button>
                    </form>

                    {!hackerrankStats && <p style={{color: '#8b949e'}}>Search a username to load HackerRank data.</p>}

                    {hackerrankChartData && (
                        <div style={{marginBottom: '1rem', padding: '1rem', background: '#0d1117', borderRadius: '8px'}}>
                            <h3 style={{margin: '0 0 10px 0', textAlign: 'center'}}>Solved Challenges: {hackerrankSolved}</h3>
                            <div style={{height: '220px', display: 'flex', justifyContent: 'center'}}>
                                <ChartView type="doughnut" data={hackerrankChartData} options={darkDoughnutOptions} />
                            </div>
                            <div style={{marginTop: '1rem', padding: '0.8rem', backgroundColor: '#161b22', borderRadius: '6px', fontSize: '0.9rem', color: '#c9d1d9'}}>
                                <strong style={{color: 'var(--primary-color)'}}>💡 Insights:</strong>
                                <ul style={{marginTop: '5px', paddingLeft: '20px', listStyleType: 'none', marginLeft: '-20px', marginBottom: '0'}}>
                                    <li style={{marginBottom: '4px'}}>Rank: {hackerrankStats.rank || 'N/A'}</li>
                                    <li style={{marginBottom: '4px'}}>Badges: {Array.isArray(hackerrankStats.badges) ? hackerrankStats.badges.length : 0}</li>
                                </ul>
                            </div>
                        </div>
                    )}
                </div>

                {/* GitHub Integration Panel */}
                <div className="card" style={{gridColumn: '1 / -1'}}>
                    <h2>GitHub Activity</h2>
                    <form onSubmit={(e) => handleSync(e, 'github')} style={{display: 'flex', gap: '8px', marginBottom: '1rem', width: '100%'}}>
                        <input style={{flex: 1, padding:'0.5rem', borderRadius: '4px', border: '1px solid var(--border-color)', background: '#0d1117', color: 'white'}} type="text" value={githubUsername} onChange={e => setGithubUsername(e.target.value)} placeholder="GitHub Username" required/>
                        <button type="submit" className="btn" style={{width: '80px'}} disabled={loading==='github'}>{loading === 'github' ? '...' : 'Sync'}</button>
                    </form>
                    
                    {(!githubData || githubData.length === 0) && <p style={{color: '#8b949e'}}>No recent GitHub events found for {githubUsername}.</p>}
                    
                    <div style={{display: 'flex', gap: '1rem', overflowX: 'auto'}}>
                        {(Array.isArray(githubData) ? githubData : []).slice(0, 15).map((event, idx) => (
                            <div key={idx} className="integration-item" style={{minWidth: '250px'}}>
                                <div><strong>Type:</strong> {(event?.type || '').replace('Event', '')}</div>
                                <div><strong>Repo:</strong> {event?.repo?.name || 'Unknown'}</div>
                                <div style={{color: '#8b949e', fontSize: '0.85rem'}}>Date: {event?.created_at ? new Date(event.created_at).toLocaleDateString() : 'N/A'}</div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);
