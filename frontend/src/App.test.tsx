import '@testing-library/jest-dom/vitest'
import userEvent from '@testing-library/user-event'
import { render, screen } from '../test-utils'
import App from './App'

describe('App', () => {
  it('renders landing title on the main page', () => {
    render(<App />)
    expect(screen.getByRole('heading', { level: 1, name: 'Календарь звонков' })).toBeInTheDocument()
    expect(
      screen.getByRole('link', { name: 'Записаться →' }),
    ).toBeInTheDocument()
  })

  it('navigates from the landing to the booking page', async () => {
    const user = userEvent.setup()
    render(<App />)
    await user.click(screen.getByRole('link', { name: 'Записаться →' }))
    expect(screen.getByRole('heading', { name: 'Запись на звонок' })).toBeInTheDocument()
    expect(screen.getByText(/Запись откроется на следующем шаге/)).toBeInTheDocument()
  })

  it('shows the booking stub when opened directly', () => {
    render(<App />, { route: '/booking' })
    expect(screen.getByRole('heading', { name: 'Запись на звонок' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: '← На главную' })).toBeInTheDocument()
  })
})
