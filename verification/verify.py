from playwright.sync_api import sync_playwright

def verify_frontend():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()

        # Mock API responses
        def handle_api_elections(route):
            route.fulfill(
                status=200,
                content_type="application/json",
                body='[{"id": 1, "name": "Presidential Election 2024", "startTime": "2024-01-01T00:00:00", "endTime": "2024-12-31T23:59:59"}]'
            )

        def handle_api_auth_me(route):
            route.fulfill(
                status=200,
                content_type="application/json",
                body='{"id": 1, "name": "Test User", "email": "test@example.com", "isAdmin": false}'
            )

        # Intercept requests
        page.route("**/api/elections", handle_api_elections)
        page.route("**/api/auth/me", handle_api_auth_me)

        # Navigate
        page.goto("http://localhost:8080/elections.html")

        # Wait for content to load
        page.wait_for_selector("#election-list li")

        # Screenshot
        page.screenshot(path="verification/verification.png")

        browser.close()

if __name__ == "__main__":
    verify_frontend()
