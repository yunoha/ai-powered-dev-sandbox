import { expect, test } from "@playwright/test";

test("top page renders the greeting", async ({ page }) => {
  await page.goto("/");

  await expect(page.getByRole("heading", { level: 1, name: "Hello world!" })).toBeVisible();
});
