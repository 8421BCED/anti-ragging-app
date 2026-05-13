-- Create tables for Joel's Antirag project

-- Table for storing complaints
CREATE TABLE complaints (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT,
    class TEXT,
    section TEXT,
    department TEXT,
    problem TEXT NOT NULL,
    image_url TEXT,
    status TEXT DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'declined')),
    is_anonymous BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Table for storing SOS alerts
CREATE TABLE sos_alerts (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    message TEXT DEFAULT 'SOS Alert Triggered!',
    status TEXT DEFAULT 'active' CHECK (status IN ('active', 'resolved')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Enable Realtime for these tables to allow the admin panel to react instantly
ALTER PUBLICATION supabase_realtime ADD TABLE complaints;
ALTER PUBLICATION supabase_realtime ADD TABLE sos_alerts;
