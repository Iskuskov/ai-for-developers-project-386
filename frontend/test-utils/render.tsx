import type { ReactNode } from 'react'
import { render as testingLibraryRender } from '@testing-library/react'
import { MemoryRouter } from 'react-router'
import { MantineProvider } from '@mantine/core'
import { theme } from '../src/theme'

export function render(ui: ReactNode, { route = '/' }: { route?: string } = {}) {
  return testingLibraryRender(<>{ui}</>, {
    wrapper: ({ children }: { children: ReactNode }) => (
      <MantineProvider theme={theme} env="test">
        <MemoryRouter initialEntries={[route]}>{children}</MemoryRouter>
      </MantineProvider>
    ),
  })
}
