import "./App.css";
import { Route, Routes } from "react-router-dom";
import NavigationHeader from "./components/layout/NavigationHeader";
import HomePage from "./pages/Landing/HomePage";
import ChatPage from "./pages/Chat/ChatPage";
import Search from "./pages/Search/Search";
import ProfileOfAgent from "./pages/Profile/agent/ProfileOfAgent";
import ProfileOfForeigner from "./pages/Profile/Foreigner/ProfileOfForeigner";
import AgentOnboard from "./pages/Onboard/AgentOnboard";
import ForeignerOnboard from "./pages/Onboard/ForeignerOnboard";
import Documents from "./pages/Documents/Documents";
import EditDocument from "./pages/Documents/EditDocument/EditDocument";
import { AuthContextProvider } from "./contexts/AuthContextProvider";
import { LocaleContextProvider } from "./contexts/LocaleContextProvider";
import MyProfile from "./pages/Profile/MyProfile";
import { WebSocketProvider } from "./contexts/WebSocketContext";
function App() {
  return (
    <AuthContextProvider>
      <LocaleContextProvider>
        <WebSocketProvider>
          <div className="w-380 relative flex flex-col justify-center overflow-x-visible">
            <NavigationHeader />

            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/search/:targetType" element={<Search />} />
              <Route path="/profile" element={<MyProfile />} />
              <Route
                path="/profile/foreigner/:foreignerId"
                element={<ProfileOfForeigner />}
              />
              <Route
                path="/profile/agent/:agentId"
                element={<ProfileOfAgent />}
              />
              <Route path="/onboard/foreigner" element={<ForeignerOnboard />} />
              <Route path="/onboard/agent" element={<AgentOnboard />} />
              <Route path="/chat" element={<ChatPage />} />
              <Route path="/documents" element={<Documents />} />
              <Route path="/document/:documentId" element={<EditDocument />} />
              <Route path="/document" element={<EditDocument />} />
            </Routes>
          </div>
        </WebSocketProvider>
      </LocaleContextProvider>
    </AuthContextProvider>
  );
}

export default App;
