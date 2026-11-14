import { Route, Routes } from 'react-router'
import { BookingPage } from './pages/BookingPage'
import { LandingPage } from './pages/LandingPage'
import { SlotCalendarPage } from './pages/SlotCalendarPage'

function App() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/booking" element={<BookingPage />} />
      <Route path="/booking/:eventTypeId" element={<SlotCalendarPage />} />
    </Routes>
  )
}

export default App
