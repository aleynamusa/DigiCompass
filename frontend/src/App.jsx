import { useState } from 'react'
import reactLogo from './assets/react.svg'
import './App.css'
import SignUpForm from "./pages/SignUp.jsx";

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
        <SignUpForm></SignUpForm>
    </>
  )
}

export default App
