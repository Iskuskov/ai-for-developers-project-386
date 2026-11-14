import { Route, Routes } from 'react-router'
import { BookingPage } from './pages/BookingPage'
import { BookingsPage } from './pages/BookingsPage'
import { EventTypesPage } from './pages/EventTypesPage'
import { LandingPage } from './pages/LandingPage'
import { SlotCalendarPage } from './pages/SlotCalendarPage'

function App() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/booking" element={<BookingPage />} />
      <Route path="/booking/:eventTypeId" element={<SlotCalendarPage />} />
      <Route path="/bookings" element={<BookingsPage />} />
      <Route path="/event-types" element={<EventTypesPage />} />
    </Routes>
  )
}

export default App
