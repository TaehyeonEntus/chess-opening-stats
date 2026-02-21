import createMiddleware from 'next-intl/middleware';
import { NextRequest, NextResponse } from 'next/server';
import { locales } from './i18n';

const AUTH_PAGES = ["/login", "/register"];

const intlMiddleware = createMiddleware({
  locales,
  defaultLocale: 'en',
  localePrefix: 'as-needed'
});

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;
  const accessToken = request.cookies.get("accessToken")?.value;
  
  // Remove locale prefix for auth check
  const pathnameWithoutLocale = pathname.replace(/^\/(en|ko)/, "") || "/";
  const isAuthPage = AUTH_PAGES.includes(pathnameWithoutLocale);

  // Auth redirect logic
  if (!accessToken && !isAuthPage) {
    const locale = pathname.startsWith('/ko') ? 'ko' : 'en';
    const loginUrl = new URL(locale === 'ko' ? '/ko/login' : '/login', request.url);
    return NextResponse.redirect(loginUrl);
  }

  if (accessToken && isAuthPage) {
    const locale = pathname.startsWith('/ko') ? 'ko' : 'en';
    const homeUrl = new URL(locale === 'ko' ? '/ko' : '/', request.url);
    return NextResponse.redirect(homeUrl);
  }

  // Apply intl middleware
  return intlMiddleware(request);
}

export const config = {
  matcher: ['/', '/(ko|en)/:path*', '/((?!_next|_vercel|api|.*\\..*).*)']
};
