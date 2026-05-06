import { Link, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import JobIcon, { useJobLabels } from '../../../assets/JobIcon';
import { useAuth } from '../../../contexts/AuthContextProvider';
import { alertT } from '../../../i18n/alerts';

const ExploreJobs = ({ isAgent = false }) => {
	const { t } = useTranslation(['pages']);
	const jobLabels = useJobLabels();
	const { userType } = useAuth();
	const navigate = useNavigate();

	const handleJobClick = (e: React.MouseEvent, index: number) => {
		if (!userType || userType === 'NOT_AUTHED') {
			e.preventDefault();
			alertT('pages.landing.loginRequired');
			return;
		}
		navigate(
			`/search/${isAgent ? 'foreigner' : 'agent'}${index === 15 ? '' : `?job=${index}`}`,
		);
	};

	return (
		<div className="flex flex-col gap-5 mt-17">
			<h2 className="headline-s-bold">
				{t(isAgent ? 'landing.exploreForeignersByJob' : 'landing.exploreJobs')}
			</h2>
			<div className="grid grid-rows-2 grid-cols-8 px-5 pb-5 gap-4">
				{Array.from({ length: 16 }).map((_, i) => (
					<Link
						key={`job_${i}`}
						className="flex flex-col items-center body-l-semibold cursor-pointer whitespace-nowrap
              transition-all duration-150 ease-out hover:scale-115"
						to={`/search/${isAgent ? 'foreigner' : 'agent'}${i === 15 ? '' : `?job=${i}`}`}
						onClick={(e) => handleJobClick(e, i)}
					>
						<JobIcon index={i} />
						{jobLabels[i]}
					</Link>
				))}
			</div>
		</div>
	);
};

export default ExploreJobs;
