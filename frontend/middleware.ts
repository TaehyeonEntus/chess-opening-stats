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
  
  // 세션 기반 인증에서는 XSRF-TOKEN만으로는 로그인 여부를 알 수 없습니다.
  // (AuthInitializer가 로그인 전에도 보안을 위해 이 토큰을 가져오기 때문입니다.)
  // 실제 로그인 성공 시 서버가 주는 JSESSIONID 또는 별도의 인증 쿠키(예: isLoggedIn)를 확인해야 합니다.
  const sessionCookie = request.cookies.get("JSESSIONID")?.value;
  
  // Remove locale prefix for auth check
  const pathnameWithoutLocale = pathname.replace(/^\/(en|ko)/, "") || "/";
  const isAuthPage = AUTH_PAGES.includes(pathnameWithoutLocale);

  // Auth redirect logic
  if (!sessionCookie && !isAuthPage) {
    const locale = pathname.startsWith('/ko') ? 'ko' : 'en';
    const loginUrl = new URL(locale === 'ko' ? '/ko/login' : '/login', request.url);
    return NextResponse.redirect(loginUrl);
  }

  if (sessionCookie && isAuthPage) {
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
