import { useState } from 'react'
import './App.css'

function SignUp() {
    function signupAction() {

    }

    return (
        <div>
            <form onSubmit={signupAction} className="space-y-4">
                <div className="space-y-2">
                    <Label htmlFor="email">Email</Label>
                    <Input
                        id="email"
                        type="email"
                        placeholder="your@email.com"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>

                <div className="space-y-2">
                    <Label htmlFor="password">Password</Label>
                    <div className="relative">
                        <Input
                            id="password"
                            type={showPassword ? "text" : "password"}
                            placeholder="Enter your password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                        <Button
                            type="button"
                            variant="ghost"
                            size="sm"
                            className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                            onClick={() => setShowPassword(!showPassword)}
                        >
                            {showPassword ? <EyeOffIcon className="h-4 w-4" /> : <EyeIcon className="h-4 w-4" />}
                        </Button>
                    </div>
                </div>

                <Button type="submit" className="w-full" disabled={isLoading}>
                    {isLoading ? "Signing in..." : "Sign In"}
                </Button>

                <div className="text-center">
                    <Button type="button" variant="link" className="text-sm" onClick={onSwitchToSignup}>
                        {"Don't have an account? Sign up"}
                    </Button>
                </div>
            </form>
        </div>
    );
}

export default SignUp
