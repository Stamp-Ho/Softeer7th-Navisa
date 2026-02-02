import { Route, Routes } from "react-router-dom";
import "./App.css";
import HomePage from "./pages/Landing/HomePage";
import ProfileOfForeigner from "./pages/Profile/Foreigner/ProfileOfForeigner";
import NavigationHeader from "./components/shared/NavigationHeader";
import SearchAgent from "./pages/Search/SearchAgent";
import ForeignerOnboard from "./pages/Onboard/ForeignerOnboard";
import AgentOnboard from "./pages/Onboard/AgentOnboard";
import ProfileOfAgent from "./pages/Profile/Agent/ProfileOfAgent";
import Documents from "./pages/Documents/Documents";
import EditDocument from "./pages/Documents/EditDocument";
import SearchForeigner from "./pages/Search/SearchForeigner";

function App() {
  return (
    <div className="w-380 relative flex flex-col justify-center overflow-x-visible">
      <NavigationHeader />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/search/agent" element={<SearchAgent />} />
        <Route path="/search/foreigner" element={<SearchForeigner />} />
        <Route
          path="/profile/foreigner/:foreignerId"
          element={<ProfileOfForeigner />}
        />
        <Route path="/onboard/foreigner" element={<ForeignerOnboard />} />
        <Route path="/onboard/agent" element={<AgentOnboard />} />
        <Route path="/profile/agent/:agentId" element={<ProfileOfAgent />} />
        <Route path="/documents" element={<Documents />} />
        <Route path="/document/:documentId" element={<EditDocument />} />
      </Routes>
    </div>
  );
}

export default App;
