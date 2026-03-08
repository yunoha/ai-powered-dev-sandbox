import { getGreeting } from "../lib/getGreeting";

export default async function HomePage() {
  const greeting = await getGreeting();

  return (
    <main>
      <article>
        <h1>{greeting.message}</h1>
      </article>
    </main>
  );
}
