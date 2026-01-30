import Tag from "../../../components/common/Tag";
import { jobCodeList } from "../../../types/job";
import { nationList } from "../../../types/nations";

type AgentBlogData = {
  blogList: AgentBlog[];
  name: string;
  profileImageUrl: string;
};

type AgentBlog = {
  blogId: number;
  specialityId: number;
  nationId: number;
  languageId: number;
  title: string;
  content: string;
};

const AgentBlog = ({ blogList, name, profileImageUrl }: AgentBlogData) => {
  return (
    <div>
      <div className="headline-m-semibold text-gray-1000 mb-7">
        행정사의 블로그
      </div>
      {blogList.map((data, idx) => (
        <div key={data.blogId} className="flex flex-col cursor-pointer">
          <div className="flex flex-col gap-6 py-6">
            <div className="flex flex-row gap-2 items-center">
              <Tag type="small_fill_violet_max">
                {jobCodeList[data.specialityId]}
              </Tag>
              <Tag type="small_fill_green_max">{nationList[data.nationId]}</Tag>
            </div>
            <div>
              <div className="flex-wrap whitespace-pre-line title-m-bold color-text-base mb-4 whitespace-normal lg:truncate lg:max-w-[1008px]">
                {data.title}
              </div>
              <div className="flex-wrap whitespace-pre-line body-l-medium color-text-base whitespace-normal lg:truncate lg:max-w-[1008px]">
                {data.content}
              </div>
            </div>
            <div className="flex flex-row gap-2 items-center">
              <img
                className="w-[26px] h-[26px] object-cover rounded-full"
                src="https://placehold.co/748x462"
                alt={`${name} 프로필 이미지`}
              />
              <div className="body-s-medium text-text-sub">{name}</div>
            </div>
          </div>
          {idx < 2 && (
            <div className="w-full pt-[1px] bg-border-normal my-6"></div>
          )}
        </div>
      ))}
    </div>
  );
};

export default AgentBlog;
