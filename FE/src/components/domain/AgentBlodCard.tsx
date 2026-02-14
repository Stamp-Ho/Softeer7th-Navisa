import { jobCodeList } from "../../constants/job";
import { nationList } from "../../constants/nations";
import Tag from "../common/Tag";

type AgentBlodCardProps = {
  blog: AgentBlogCardContent;
  name: string;
  profileImageUrl: string;
};

type AgentBlogCardContent = {
  title: string;
  content: string;
  specialityId: number;
  nationId: number;
};

const AgentBlogCard = ({ blog, name, profileImageUrl }: AgentBlodCardProps) => {
  return (
    <div className="flex flex-col gap-6 py-6">
      <div className="flex flex-row gap-2 items-center">
        <Tag type="small_fill_violet_max">{jobCodeList[blog.specialityId]}</Tag>
        <Tag type="small_fill_green_max">{nationList[blog.nationId]}</Tag>
      </div>
      <div>
        <div className="flex-wrap whitespace-pre-line title-m-bold color-text-base mb-4 whitespace-normal lg:truncate lg:max-w-[1008px]">
          {blog.title}
        </div>
        <div className="flex-wrap whitespace-pre-line body-l-medium color-text-base whitespace-normal lg:truncate lg:max-w-[1008px]">
          {blog.content}
        </div>
      </div>
      <div className="flex flex-row gap-2 items-center">
        <img
          className="w-[26px] h-[26px] object-cover rounded-full"
          src={profileImageUrl}
          alt={"https://placehold.co/748x462"}
        />
        <div className="body-s-medium text-text-sub">{name}</div>
      </div>
    </div>
  );
};

export default AgentBlogCard;
