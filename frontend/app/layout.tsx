import { ReactNode } from 'react'

// This is a root layout that just passes through to the locale layout
export default function RootLayout({
  children,
}: {
  children: ReactNode
}) {
  return children
}
