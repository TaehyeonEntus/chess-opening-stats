import createMiddleware from 'next-intl/middleware';
import { locales } from './i18n';

export default createMiddleware({
  locales,
  defaultLocale: 'en',
  localePrefix: 'as-needed'
});

export const config = {
  matcher: ['/', '/(ko|en)/:path*', '/((?!_next|_vercel|api|.*\\..*).*)']
};
