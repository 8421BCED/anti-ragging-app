import React, { useState } from 'react'
import axios from 'axios'

function Login({ onLogin }) {
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      const response = await axios.post('http://localhost:5000/api/login', { password })
      if (response.data.success) {
        localStorage.setItem('admin_token', response.data.token)
        onLogin()
      }
    } catch (err) {
      setError('Invalid password. Please try again.')
    }
  }

  return (
    <div className="flex flex-col items-center justify-center min-h-[80vh]">
      <div className="glass p-8 w-full max-w-md shadow-2xl">
        <h1 className="text-3xl font-bold text-center mb-6 text-white">Joel's Antirag Admin</h1>
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <input
            type="password"
            placeholder="Enter Admin Password"
            className="p-3 rounded-lg bg-slate-800 border border-slate-700 text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          {error && <p className="text-red-400 text-sm">{error}</p>}
          <button type="submit" className="btn-primary mt-2">Login</button>
        </form>
      </div>
    </div>
  )
}

export default Login
