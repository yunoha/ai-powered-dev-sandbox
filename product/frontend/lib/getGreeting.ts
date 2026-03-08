export type GreetingResponse = {
  message: string;
};

export async function getGreeting(): Promise<GreetingResponse> {
  const backendBaseUrl = process.env.BACKEND_BASE_URL ?? "http://127.0.0.1:8080";
  const response = await fetch(`${backendBaseUrl}/api/greeting`, {
    cache: "no-store",
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch greeting: ${response.status}`);
  }

  return (await response.json()) as GreetingResponse;
}
