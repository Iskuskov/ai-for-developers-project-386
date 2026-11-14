import { Route, Routes } from 'react-router'
import { BookingPage } from './pages/BookingPage'
import { EventTypesPage } from './pages/EventTypesPage'
import { LandingPage } from './pages/LandingPage'

function App() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/booking" element={<BookingPage />} />
      <Route path="/event-types" element={<EventTypesPage />} />
    </Routes>
  )
}

export default App
