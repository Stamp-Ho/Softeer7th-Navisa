import "./App.css";
import { Route, Routes } from "react-router-dom";
import NavigationHeader from "./components/shared/NavigationHeader";
import HomePage from "./pages/Landing/HomePage";
import SearchAgent from "./pages/Search/SearchAgent";
import SearchForeigner from "./pages/Search/SearchForeigner";
import ProfileOfAgent from "./pages/Profile/agent/ProfileOfAgent";
import ProfileOfForeigner from "./pages/Profile/Foreigner/ProfileOfForeigner";
import AgentOnboard from "./pages/Onboard/AgentOnboard";
import ForeignerOnboard from "./pages/Onboard/ForeignerOnboard";
import Documents from "./pages/Documents/Documents";
import EditDocument from "./pages/Documents/EditDocument/EditDocument";

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
        <Route path="/profile/agent/:agentId" element={<ProfileOfAgent />} />
        <Route path="/onboard/foreigner" element={<ForeignerOnboard />} />
        <Route path="/onboard/agent" element={<AgentOnboard />} />
        <Route path="/documents" element={<Documents />} />
        <Route path="/document/:documentId" element={<EditDocument />} />
      </Routes>
    </div>
  );
}

export default App;
