@import com.procuredox.login.user.SignupForm
@import org.springframework.security.web.csrf.CsrfToken
@import java.util.Map
@param CsrfToken csrfToken
@param SignupForm form
@param Map<String, String> errors
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Sign up</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="/css/output.css" rel="stylesheet">
</head>
<body class="min-h-screen bg-gray-100 flex items-center justify-center p-4">

<form method="post" action="/signup" novalidate
      class="w-full max-w-md bg-white p-8 rounded-lg shadow-md space-y-3">
    <input type="hidden" name="${csrfToken.getParameterName()}" value="${csrfToken.getToken()}">
    <h1 class="text-2xl font-bold text-center">Create account</h1>

    <div class="grid grid-cols-2 gap-3">
        <div>
            <label for="firstName" class="block text-sm font-medium text-gray-700">First name</label>
            <input type="text" id="firstName" name="firstName" autocomplete="given-name" value="${form.firstName()}"
                   class="mt-1 w-full rounded border px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.containsKey("firstName") ? "border-red-500" : "border-gray-300"}">
            @if(errors.containsKey("firstName"))
                <p class="mt-1 text-xs text-red-600">${errors.get("firstName")}</p>
            @endif
        </div>
        <div>
            <label for="lastName" class="block text-sm font-medium text-gray-700">Last name</label>
            <input type="text" id="lastName" name="lastName" autocomplete="family-name" value="${form.lastName()}"
                   class="mt-1 w-full rounded border px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.containsKey("lastName") ? "border-red-500" : "border-gray-300"}">
            @if(errors.containsKey("lastName"))
                <p class="mt-1 text-xs text-red-600">${errors.get("lastName")}</p>
            @endif
        </div>
    </div>

    <div>
        <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
        <input type="email" id="email" name="email" autocomplete="username" value="${form.email()}"
               class="mt-1 w-full rounded border px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.containsKey("email") ? "border-red-500" : "border-gray-300"}">
        @if(errors.containsKey("email"))
            <p class="mt-1 text-xs text-red-600">${errors.get("email")}</p>
        @endif
    </div>

    <div>
        <label for="phone" class="block text-sm font-medium text-gray-700">Phone number</label>
        <input type="tel" id="phone" name="phone" autocomplete="tel" value="${form.phone()}"
               class="mt-1 w-full rounded border px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.containsKey("phone") ? "border-red-500" : "border-gray-300"}">
        @if(errors.containsKey("phone"))
            <p class="mt-1 text-xs text-red-600">${errors.get("phone")}</p>
        @endif
    </div>

    <div>
        <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
        <input type="password" id="password" name="password" autocomplete="new-password"
               class="mt-1 w-full rounded border px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.containsKey("password") ? "border-red-500" : "border-gray-300"}">
        @if(errors.containsKey("password"))
            <p class="mt-1 text-xs text-red-600">${errors.get("password")}</p>
        @endif
    </div>

    <div>
        <label for="confirmPassword" class="block text-sm font-medium text-gray-700">Confirm password</label>
        <input type="password" id="confirmPassword" name="confirmPassword" autocomplete="new-password"
               class="mt-1 w-full rounded border px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 ${errors.containsKey("confirmPassword") ? "border-red-500" : "border-gray-300"}">
        @if(errors.containsKey("confirmPassword"))
            <p class="mt-1 text-xs text-red-600">${errors.get("confirmPassword")}</p>
        @endif
    </div>

    <button type="submit"
            class="w-full rounded bg-green-600 py-2 font-medium text-white hover:bg-green-700">
        Sign up
    </button>

    <p class="text-center text-sm text-gray-600">
        Already have an account? <a href="/login" class="text-blue-600 hover:underline">Sign in</a>
    </p>
</form>

</body>
</html>