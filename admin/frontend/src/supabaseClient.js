import { createClient } from '@supabase/supabase-js'

const supabaseUrl = 'https://bmtslqeyilbxtyjshncd.supabase.co'
const supabaseKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJtdHNscWV5aWxieHR5anNobmNkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzg2OTIxNzMsImV4cCI6MjA5NDI2ODE3M30.i4R4HAjTzIPiDL2WddMjTxAn3nwhpqgKZDGpfd0umZo'

export const supabase = createClient(supabaseUrl, supabaseKey)
