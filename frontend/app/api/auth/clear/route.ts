import { NextResponse } from "next/server";

export async function GET() {
  const response = NextResponse.redirect(new URL("/login", process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:3001"));

  // 서버 측에서 HttpOnly 쿠키 강제 삭제 명령 발송
  const cookieOptions = "path=/; Max-Age=0; expires=Thu, 01 Jan 1970 00:00:00 GMT;";
  
  // 보안을 위해 SameSite 및 HttpOnly 속성도 포함하여 지우는 것이 좋습니다.
  response.headers.append("Set-Cookie", `JSESSIONID=; ${cookieOptions} HttpOnly; SameSite=Lax`);
  response.headers.append("Set-Cookie", `XSRF-TOKEN=; ${cookieOptions} SameSite=Lax`);

  return response;
}
