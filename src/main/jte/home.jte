@import org.springframework.security.web.csrf.CsrfToken
@param CsrfToken csrfToken
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Home</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="/css/output.css" rel="stylesheet">
</head>
<body class="min-h-screen bg-gray-100">

<header class="flex items-center justify-between bg-white px-6 py-4 shadow">
    <span class="text-xl font-bold text-blue-600">Procuredox</span>

    <form method="post" action="/logout">
        <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
        <button type="submit"
                class="rounded bg-red-600 px-4 py-2 text-sm font-medium text-white hover:bg-red-700">
            Logout
        </button>
    </form>
</header>

<main class="p-6">
    <h1 class="text-3xl font-bold">Welcome</h1>
</main>

</body>
</html>