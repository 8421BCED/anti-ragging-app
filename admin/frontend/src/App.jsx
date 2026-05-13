import React, { useState, useEffect } from 'react'
import Login from './components/Login'
import Dashboard from './components/Dashboard'

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)

  useEffect(() => {
    const token = localStorage.getItem('admin_token')
    if (token) setIsLoggedIn(true)
  }, [])

  return (
    <div className="min-h-screen p-8">
      {!isLoggedIn ? (
        <Login onLogin={() => setIsLoggedIn(true)} />
      ) : (
        <Dashboard onLogout={() => {
          localStorage.removeItem('admin_token')
          setIsLoggedIn(false)
        }} />
      )}
    </div>
  )
}

export default App
