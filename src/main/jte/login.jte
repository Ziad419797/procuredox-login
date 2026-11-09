@import org.springframework.security.web.csrf.CsrfToken
@param CsrfToken csrfToken
@param String error = null
@param String success = null

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="/css/output.css" rel="stylesheet">
</head>
<body class="min-h-screen bg-gray-100 flex items-center justify-center">

<form method="post" action="/login"
      class="w-full max-w-sm bg-white p-8 rounded-lg shadow-md space-y-3">
    <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
    <h1 class="text-2xl font-bold text-center">Login</h1>

    @if(error != null)
        <p class="rounded bg-red-100 px-3 py-2 text-sm text-red-700">${error}</p>
    @endif

    @if(success != null)
        <p class="rounded bg-green-100 px-3 py-2 text-sm text-green-700">${success}</p>
    @endif

    <div>
        <label for="username" class="block text-sm font-medium text-gray-700">Email</label>
        <input type="email" id="username" name="username" autocomplete="username" required
               class="mt-1 w-full rounded border border-gray-300 px-3 py-2
                          focus:outline-none focus:ring-2 focus:ring-blue-500">
    </div>

    <div>
        <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
        <input type="password" id="password" name="password" autocomplete="current-password" required
               class="mt-1 w-full rounded border border-gray-300 px-3 py-2
                          focus:outline-none focus:ring-2 focus:ring-blue-500">
    </div>

    <button type="submit"
            class="w-full rounded bg-green-600 py-2 font-medium text-white hover:bg-green-700">
        Sign in
    </button>

    <p class="text-center text-sm text-gray-600">
        No account? <a href="/signup" class="text-blue-600 hover:underline">Create one</a>
    </p>

</form>

</body>
</html>