@import org.springframework.security.web.csrf.CsrfToken
@param CsrfToken csrfToken
@param String error = null
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Verification</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="/css/output.css" rel="stylesheet">
</head>
<body class="min-h-screen bg-gray-100 flex items-center justify-center p-4">

<form method="post" action="/otp" class="w-full max-w-sm bg-white p-8 rounded-lg shadow-md space-y-4">
    <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
    <h1 class="text-2xl font-bold text-center">Verification code</h1>
    <p class="text-sm text-gray-600 text-center">
        Enter the 6-digit code printed in the server terminal. It is valid for 5 minutes.
    </p>

    @if(error != null)
        <p class="rounded bg-red-100 px-3 py-2 text-sm text-red-700">${error}</p>
    @endif

    <input type="text" id="code" name="code" inputmode="numeric" autocomplete="one-time-code"
           maxlength="6" autofocus
           class="w-full rounded border border-gray-300 px-3 py-2 text-center text-2xl tracking-widest focus:outline-none focus:ring-2 focus:ring-blue-500">

    <button type="submit" class="w-full rounded bg-green-600 py-2 font-medium text-white hover:bg-green-700">
        Verify
    </button>
</form>

</body>
</html>