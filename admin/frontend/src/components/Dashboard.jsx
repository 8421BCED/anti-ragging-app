import React, { useState, useEffect } from 'react'
import { supabase } from '../supabaseClient'
import { AlertCircle, FileText, CheckCircle, XCircle, LogOut, Trash2 } from 'lucide-react'

function Dashboard({ onLogout }) {
  const [sosAlerts, setSosAlerts] = useState([])
  const [complaints, setComplaints] = useState([])

  useEffect(() => {
    fetchInitialData()

    // Real-time subscriptions
    const sosSubscription = supabase
      .channel('sos_alerts')
      .on('postgres_changes', { event: '*', schema: 'public', table: 'sos_alerts' }, payload => {
        fetchInitialData()
      })
      .subscribe()

    const complaintSubscription = supabase
      .channel('complaints')
      .on('postgres_changes', { event: '*', schema: 'public', table: 'complaints' }, payload => {
        fetchInitialData()
      })
      .subscribe()

    return () => {
      supabase.removeChannel(sosSubscription)
      supabase.removeChannel(complaintSubscription)
    }
  }, [])

  const fetchInitialData = async () => {
    const { data: sos } = await supabase.from('sos_alerts').select('*').order('created_at', { ascending: false })
    const { data: comp } = await supabase.from('complaints').select('*').order('created_at', { ascending: false })
    setSosAlerts(sos || [])
    setComplaints(comp || [])
  }

  const handleStatusChange = async (id, status) => {
    await supabase.from('complaints').update({ status }).eq('id', id)
    fetchInitialData()
  }

  const handleDeleteSos = async (id) => {
    if (window.confirm('Clear this SOS alert?')) {
      await supabase.from('sos_alerts').delete().eq('id', id)
      fetchInitialData()
    }
  }

  const handleDeleteComplaint = async (id) => {
    if (window.confirm('Delete this complaint record?')) {
      await supabase.from('complaints').delete().eq('id', id)
      fetchInitialData()
    }
  }

  return (
    <div className="max-w-6xl mx-auto">
      <header className="flex justify-between items-center mb-8 glass p-6">
        <h1 className="text-2xl font-bold text-white flex items-center gap-2">
          <AlertCircle className="text-blue-500" /> FX AntiRag Admin
        </h1>
        <button onClick={onLogout} className="flex items-center gap-2 text-slate-400 hover:text-white transition">
          <LogOut size={20} /> Logout
        </button>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* SOS Alerts Section */}
        <section className="glass p-6">
          <h2 className="text-xl font-semibold mb-4 text-red-400 flex items-center gap-2">
            <AlertCircle /> Active SOS Alerts
          </h2>
          <div className="flex flex-col gap-4 max-h-[600px] overflow-y-auto pr-2">
            {sosAlerts.length === 0 && <p className="text-slate-500 text-center">No active alerts</p>}
            {sosAlerts.map(alert => (
              <div key={alert.id} className="bg-slate-800 p-4 rounded-xl border-l-4 border-red-500 shadow-lg relative group">
                <div className="flex justify-between items-start">
                  <div>
                    <p className="font-bold text-white">Location: {alert.latitude}, {alert.longitude}</p>
                    <p className="text-sm text-slate-400">{new Date(alert.created_at).toLocaleString()}</p>
                  </div>
                  <div className="flex flex-col items-end gap-2">
                    <a 
                      href={`https://www.google.com/maps?q=${alert.latitude},${alert.longitude}`}
                      target="_blank"
                      className="text-blue-400 text-sm hover:underline"
                    >
                      View Map
                    </a>
                    <button 
                      onClick={() => handleDeleteSos(alert.id)}
                      className="text-slate-500 hover:text-red-500 transition p-1"
                    >
                      <Trash2 size={18} />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </section>

        {/* Complaints Section */}
        <section className="glass p-6">
          <h2 className="text-xl font-semibold mb-4 text-blue-400 flex items-center gap-2">
            <FileText /> Recent Complaints
          </h2>
          <div className="flex flex-col gap-4 max-h-[600px] overflow-y-auto pr-2">
            {complaints.length === 0 && <p className="text-slate-500 text-center">No complaints registered</p>}
            {complaints.map(comp => (
              <div key={comp.id} className="bg-slate-800 p-4 rounded-xl shadow-lg relative">
                <div className="flex justify-between items-start mb-2">
                  <div>
                    <span className={`text-xs px-2 py-1 rounded-full ${
                      comp.status === 'approved' ? 'bg-green-900 text-green-300' :
                      comp.status === 'declined' ? 'bg-red-900 text-red-300' : 'bg-yellow-900 text-yellow-300'
                    }`}>
                      {comp.status.toUpperCase()}
                    </span>
                    <h3 className="font-bold text-white mt-1">
                      {comp.is_anonymous ? 'Anonymous Report' : comp.name}
                    </h3>
                    {!comp.is_anonymous && (
                      <p className="text-xs text-slate-400">
                        Roll No: {comp.roll_no} | Year: {comp.year} | Dept: {comp.department} | Sec: {comp.section}
                      </p>
                    )}
                  </div>
                  <button 
                    onClick={() => handleDeleteComplaint(comp.id)}
                    className="text-slate-500 hover:text-red-500 transition"
                  >
                    <Trash2 size={18} />
                  </button>
                </div>
                <p className="text-slate-300 text-sm mb-3 border-t border-slate-700 pt-2">{comp.problem}</p>
                {comp.image_url && (
                  <img src={comp.image_url} alt="Proof" className="w-full h-48 object-cover rounded-lg mb-3 cursor-pointer" onClick={() => window.open(comp.image_url)} />
                )}
                {comp.status === 'pending' && (
                  <div className="flex gap-2">
                    <button 
                      onClick={() => handleStatusChange(comp.id, 'approved')}
                      className="btn-success flex-1 flex items-center justify-center gap-1"
                    >
                      <CheckCircle size={16} /> Approve
                    </button>
                    <button 
                      onClick={() => handleStatusChange(comp.id, 'declined')}
                      className="btn-danger flex-1 flex items-center justify-center gap-1"
                    >
                      <XCircle size={16} /> Decline
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  )
}

export default Dashboard
