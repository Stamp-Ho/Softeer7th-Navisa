import { Route, Routes } from "react-router-dom";
import "./App.css";
import HomePage from "./pages/Landing/HomePage";
import ProfileOfClient from "./pages/Profile/client/ProfileOfClient";
import NavigationHeader from "./components/shared/NavigationHeader";
import SearchAgent from "./pages/Search/SearchAgent";
import ClientOnboard from "./pages/Onboard/ClientOnboard";
import AgentOnboard from "./pages/Onboard/AgentOnboard";
import ProfileOfAgent from "./pages/Profile/agent/ProfileOfAgent";

function App() {
  return (
    <div className="w-380 relative flex flex-col justify-center">
      <NavigationHeader />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/search/agent" element={<SearchAgent />} />
        <Route path="/profile/client/:clientId" element={<ProfileOfClient />} />
        <Route path="/onboard/client" element={<ClientOnboard />} />
        <Route path="/onboard/agent" element={<AgentOnboard />} />
        <Route path="/profile/agent/:agentId" element={<ProfileOfAgent />} />
      </Routes>
    </div>
  );
}

export default App;
