import LoginForm from "@/components/auth/loginForm.jsx";

export default function LoginPopup({
                                       show,
                                       onClose,
                                       loginData,
                                       handleChange,
                                       handleSubmit,
                                       isChecked,
                                       setChecked,
                                       errors
                                   }) {
    if (!show) return null;

    return (
        <div className="fixed inset-0 z-[10000] flex items-center justify-center">
            <div className="fixed inset-0 bg-black/50 backdrop-blur-sm" onClick={onClose} />

            <div className="relative bg-cyan-700 rounded-md shadow-2xl p-5 w-full max-w-[500px] z-[10001]">
                <h1 className="pb-2 text-xl font-semibold text-white text-center">Log In</h1>

                <LoginForm
                    loginData={loginData}
                    handleChange={handleChange}
                    handleSubmit={handleSubmit}
                    isChecked={isChecked}
                    setChecked={setChecked}
                    errors={errors}
                />
            </div>
        </div>
    );
}
