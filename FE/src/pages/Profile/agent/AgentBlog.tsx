import { useTranslation } from "react-i18next";
import AgentBlogCard from "../../../components/domain/AgentBlodCard";

type AgentBlog = {
  blogId: number;
  specialityId: number;
  nationId: number;
  languageId: number;
  title: string;
  content: string;
};

const AgentBlog = ({
  blogList,
  agentInfo = {
    name: "엄경례",
    profileImageUrl: "https://placehold.co/368x452",
  },
}: {
  blogList: AgentBlog[];
  agentInfo?: { name: string; profileImageUrl: string };
}) => {
  const { t } = useTranslation(["pages"]);
  return (
    <div>
      <div className="headline-m-semibold text-gray-1000 mb-7">{t("profile.agentBlog")}</div>
      {blogList.map((data, idx) => (
        <div key={data.blogId} className="flex flex-col">
          <AgentBlogCard blog={data} name={agentInfo.name} profileImageUrl={agentInfo.profileImageUrl} />
          {idx < 2 && <div className="w-full pt-px bg-border-normal my-6"></div>}
        </div>
      ))}
    </div>
  );
};

export default AgentBlog;
