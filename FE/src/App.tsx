import { Route, Routes } from "react-router-dom";
import "./App.css";
import HomePage from "./pages/Landing/HomePage";
import ProfileOfClient from "./pages/Profile/client/ProfileOfClient";
import NavigationHeader from "./components/shared/NavigationHeader";

function App() {
  return (
    <div className="w-380 relative flex flex-col justify-center">
      <NavigationHeader />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/profile/client/:clientId" element={<ProfileOfClient />} />
      </Routes>
    </div>
  );
}

export default App;
