import '@testing-library/jest-dom/vitest'
import { render, screen } from '../test-utils'
import App from './App'

describe('App', () => {
  it('renders application title', () => {
    render(<App />)
    expect(screen.getByRole('heading', { name: 'Календарь звонков' })).toBeInTheDocument()
  })
})
