import NavisaLogo from '../../../assets/NavisaLogo';
import { useTranslation } from 'react-i18next';
import { DEMO_MODE } from '../../../config/demoMode';

const ForeignerBanner = () => {
	const { t } = useTranslation(['pages']);

	return (
		<div className="flex flex-col gap-9 my-50">
			{DEMO_MODE && (
				<p className="body-m-medium text-amber-700 bg-amber-50 border border-amber-200 rounded-lg px-4 py-2 text-center -mt-15">
					이 페이지는 <strong>Navisa 데모 사이트</strong>입니다 — 실제 서버 없이
					모든 화면을 체험할 수 있습니다.&nbsp; 실제 서비스는{' '}
					<a
						href="https://www.navisa.site"
						target="_blank"
						rel="noopener noreferrer"
						className="underline mx-2 title-l-medium"
					>
						navisa.site
					</a>
					에서 확인하세요.
				</p>
			)}
			<NavisaLogo height={12} />
			<div className="flex flex-col gap-3">
				<h2 className="banner-title">{t('landing.foreignerBanner')}</h2>
				<p className="headline-m-medium text-gray-600">
					{t('landing.foreignerDescription')}
				</p>
			</div>
		</div>
	);
};

export default ForeignerBanner;
