import { DEMO_DELAY_MS } from '../../config/demoMode';
import type { BaseResponse, PageResponse, UserType } from '../types/common';
import type {
	AgentBadgeReviewResponse,
	AgentCardResponse,
	AgentProfileDetailResponse,
	AgentRecentFeedbackResponse,
} from '../types/agent';
import type {
	ForeignerCardResponse,
	ForeignerMyProfileResponse,
} from '../types/foreigner';
import type {
	ChatHistoryResponse,
	ChatPageResponse,
	ChatParticipantsInfo,
	ChatRoomResponse,
} from '../types/chat';
import type {
	ApplicationFormResponse,
	RecentVisaFormsResponse,
} from '../types/etc';

type DemoMethod = 'GET' | 'POST' | 'PATCH' | 'PUT' | 'DELETE';

const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

const now = () => new Date().toISOString();

const ok = <T>(result: T, message = 'DEMO_OK'): BaseResponse<T> => ({
	code: 200,
	message,
	result,
});

const parseBody = (body?: BodyInit | null): Record<string, any> => {
	if (!body || typeof body !== 'string') return {};
	try {
		return JSON.parse(body);
	} catch {
		return {};
	}
};

const agentCards: AgentCardResponse[] = [
	{
		agentId: 'agent-demo-1',
		agentName: 'Kim',
		profileImgUrl: 'https://picsum.photos/seed/agent1/360/360',
		officeAddress: 'Seoul Gangnam-gu',
		agentSpecialityTop2: [2, 14],
		badgeTop2: [1, 4],
		specialityJobCount: 12,
	},
	{
		agentId: 'agent-demo-2',
		agentName: 'Lee',
		profileImgUrl: 'https://picsum.photos/seed/agent2/360/360',
		officeAddress: 'Busan Haeundae-gu',
		agentSpecialityTop2: [9, 23],
		badgeTop2: [2, 5],
		specialityJobCount: 8,
	},
	{
		agentId: 'agent-demo-3',
		agentName: 'Park',
		profileImgUrl: 'https://picsum.photos/seed/agent3/360/360',
		officeAddress: 'Incheon Namdong-gu',
		agentSpecialityTop2: [12, 28],
		badgeTop2: [3, 9],
		specialityJobCount: 11,
	},
	{
		agentId: 'agent-demo-4',
		agentName: 'Choi',
		profileImgUrl: 'https://picsum.photos/seed/agent4/360/360',
		officeAddress: 'Daegu Suseong-gu',
		agentSpecialityTop2: [4, 31],
		badgeTop2: [6, 10],
		specialityJobCount: 6,
	},
];

const foreignerCards: ForeignerCardResponse[] = [
	{
		foreignerId: 'foreigner-demo-1',
		nickname: 'Alex',
		nationIdList: [26, 8],
		languageIdList: [2, 3],
		jobTitle: 'Frontend Developer',
		degreeLevel: 'BACHELOR',
	},
	{
		foreignerId: 'foreigner-demo-2',
		nickname: 'Mina',
		nationIdList: [51],
		languageIdList: [1, 5],
		jobTitle: 'Data Analyst',
		degreeLevel: 'ABOVE_MASTER',
	},
	{
		foreignerId: 'foreigner-demo-3',
		nickname: 'Noah',
		nationIdList: [3],
		languageIdList: [2],
		jobTitle: 'Mechanical Engineer',
		degreeLevel: 'BELOW_BACHELOR',
	},
	{
		foreignerId: 'foreigner-demo-4',
		nickname: 'Yuna',
		nationIdList: [23, 51],
		languageIdList: [1, 4],
		jobTitle: 'Product Designer',
		degreeLevel: 'BACHELOR',
	},
];

const recentFeedbacks: AgentRecentFeedbackResponse[] = [
	{
		feedbackId: 1,
		agentName: 'Kim',
		agentProfileImgUrl: 'https://picsum.photos/seed/feedback1/120/120',
		feedbackContent: 'Very clear process and quick follow-up.',
		foreignerName: 'Alex',
		writerId: 'writer-1',
	},
	{
		feedbackId: 2,
		agentName: 'Lee',
		agentProfileImgUrl: 'https://picsum.photos/seed/feedback2/120/120',
		feedbackContent: 'Helped me complete every required section.',
		foreignerName: 'Mina',
		writerId: 'writer-2',
	},
	{
		feedbackId: 3,
		agentName: 'Park',
		agentProfileImgUrl: 'https://picsum.photos/seed/feedback3/120/120',
		feedbackContent: 'Smooth communication in English.',
		foreignerName: 'Noah',
		writerId: 'writer-3',
	},
];

const badgeReviews: AgentBadgeReviewResponse[] = [
	{
		reviewId: 1,
		reviewerInitial: 'A',
		reviewContent: 'Fast and detail-oriented consultation.',
		agentId: 'agent-demo-1',
		agentName: 'Kim',
		agentProfileImgUrl: 'https://picsum.photos/seed/badge1/120/120',
		badgeTop2: [1, 4],
	},
	{
		reviewId: 2,
		reviewerInitial: 'M',
		reviewContent: 'Great support with visa interview prep.',
		agentId: 'agent-demo-2',
		agentName: 'Lee',
		agentProfileImgUrl: 'https://picsum.photos/seed/badge2/120/120',
		badgeTop2: [2, 9],
	},
];

const recentForms: RecentVisaFormsResponse[] = [
	{
		applicationFormId: '11111111-1111-4111-8111-111111111111',
		title: 'E-7 Employment Visa',
		isDone: false,
		currentStep: 24,
		totalCount: 78,
		foreignerProfileImgUrl: 'https://picsum.photos/seed/form1/200/300',
		lastModifiedAt: now(),
	},
	{
		applicationFormId: '22222222-2222-4222-8222-222222222222',
		title: 'D-10 Job Seeker Visa',
		isDone: true,
		currentStep: 78,
		totalCount: 78,
		foreignerProfileImgUrl: null,
		lastModifiedAt: now(),
	},
];

const emptyForm = (id: string): ApplicationFormResponse => ({
	applicationFormId: id,
	foreignerProfileImgUrl: 'https://picsum.photos/seed/form-user/200/300',
	isDone: false,
	updatedAt: now(),
	totalCount: 78,
	filledCount: 0,
	sections: [],
	chatRoomId: 101,
});

const chatRooms: ChatRoomResponse[] = [
	{
		chatRoomId: 101,
		profileImgUrl: 'https://picsum.photos/seed/chat1/80/80',
		opponentName: 'Alex',
		roomStatus: 'MATCHED',
		lastMessage: 'Thanks, I will check and reply.',
		noneReadCount: 1,
		lastChattedAt: now(),
		proposed: true,
		proposalMatched: true,
	},
	{
		chatRoomId: 102,
		profileImgUrl: 'https://picsum.photos/seed/chat2/80/80',
		opponentName: 'Mina',
		roomStatus: 'DEFAULT',
		lastMessage: 'Can we discuss required documents?',
		noneReadCount: 0,
		lastChattedAt: now(),
		proposed: false,
		proposalMatched: false,
	},
];

const chatHistoryByRoom: Record<number, ChatHistoryResponse[]> = {
	101: [
		{
			chatMessageId: 1001,
			isSentByMe: false,
			type: 'TEXT',
			content: 'Hello, I am interested in your service.',
			createdAt: now(),
			isRead: true,
		},
		{
			chatMessageId: 1002,
			isSentByMe: true,
			type: 'TEXT',
			content: 'Great, I can help you start the form.',
			createdAt: now(),
			isRead: true,
		},
	],
	102: [
		{
			chatMessageId: 2001,
			isSentByMe: false,
			type: 'TEXT',
			content: 'Hi! I have a question about E-7.',
			createdAt: now(),
			isRead: false,
		},
	],
};

const participantsByRoom: Record<number, ChatParticipantsInfo> = {
	101: {
		agentInfo: {
			agentId: 'agent-demo-1',
			top2BadgeIds: [1, 4],
			name: 'Kim',
			applicationFormId: '11111111-1111-4111-8111-111111111111',
		},
		foreignerInfo: {
			foreignerId: 'foreigner-demo-1',
			nickname: 'Alex',
			expectedJob: 'Frontend Developer',
			expectedStartDate: '2026-07-01',
			nationalityIds: [26, 8],
			isReviewRequired: false,
		},
		proposalEndRequired: false,
	},
	102: {
		agentInfo: {
			agentId: 'agent-demo-2',
			top2BadgeIds: [2, 9],
			name: 'Lee',
			applicationFormId: null,
		},
		foreignerInfo: {
			foreignerId: 'foreigner-demo-2',
			nickname: 'Mina',
			expectedJob: 'Data Analyst',
			expectedStartDate: '2026-09-15',
			nationalityIds: [51],
			isReviewRequired: false,
		},
		proposalEndRequired: false,
	},
};

const agentProfileDetail: AgentProfileDetailResponse = {
	header: {
		top2badgeIds: [1, 4],
		comment: '10+ years supporting employment visas',
	},
	expertise: {
		jobCodeIds: [2, 14, 31],
		languageIds: [1, 2, 3],
	},
	agentInfo: {
		agentId: 'agent-demo-1',
		name: 'Kim',
		profileImageUrl: 'https://picsum.photos/seed/agent-profile/260/260',
		lastLoginAt: now(),
		hasChatRoom: true,
		hasBlocked: false,
		chatRoomId: 101,
	},
	additionalHistory: 'Immigration office advisor\nCorporate visa specialist',
	reviewSummary: {
		totalCount: 47,
		strengths: [
			{ badgeId: 1, badgeCount: 24 },
			{ badgeId: 4, badgeCount: 15 },
		],
	},
	officeInfo: {
		officeName: 'Navisa Demo Office',
		address: 'Seoul Gangnam-gu Teheran-ro',
		officeAddressDetail: '11F',
		businessHours: '09:30 - 18:30',
		phoneNumber: '02-1234-5678',
	},
};

const foreignerProfile: ForeignerMyProfileResponse = {
	name: 'Alex',
	nationIdList: [26, 8],
	languageIdList: [2, 3],
	education: {
		degreeLevel: 'BACHELOR',
		schoolName: 'Demo University',
		majorName: 'Computer Science',
	},
	foreignerCareers: [
		{
			companyName: 'DemoTech',
			jobTitle: 'Frontend Engineer',
			startDate: '2023-01-01',
			endDate: '2025-12-31',
			isWork: false,
		},
	],
	expectedCompany: {
		companyName: 'Seoul Startup',
		jobTitle: 'Frontend Developer',
		startDate: '2026-08-01',
	},
	isRequesting: true,
};

const asSlice = <T>(content: T[], lastElementId: string): PageResponse<T> => ({
	content,
	existsNext: false,
	lastElementId,
});

const asChatSlice = <T>(
	content: T[],
	lastElementId = 0,
): ChatPageResponse<T> => ({
	content,
	existsNext: false,
	lastElementId,
});

const isPath = (path: string, target: string) => path === target;

export const handleDemoApiRequest = async <T>(
	url: string,
	options: RequestInit,
): Promise<T> => {
	await delay(DEMO_DELAY_MS);

	const method =
		((options.method ?? 'GET').toUpperCase() as DemoMethod) || 'GET';
	const [path, queryString = ''] = url.split('?');
	const query = new URLSearchParams(queryString);
	const body = parseBody(options.body);

	if (method === 'POST' && isPath(path, '/api/auth/login')) {
		const email = String(body.email ?? '').toLowerCase();
		const userType: UserType = email.includes('agent')
			? 'VALID_AGENT'
			: email.includes('new')
				? 'UNFILLED_FOREIGNER'
				: 'FILLED_FOREIGNER';
		const userId =
			userType === 'VALID_AGENT'
				? 'demo-agent-user'
				: userType === 'UNFILLED_FOREIGNER'
					? 'demo-unfilled-user'
					: 'demo-foreigner-user';
		return ok({
			accessToken: 'demo-access-token',
			userId,
			userType,
		}) as T;
	}

	if (method === 'POST' && isPath(path, '/api/auth/signup')) {
		const userType: UserType = body.userType ?? 'FILLED_FOREIGNER';
		return ok({
			accessToken: 'demo-access-token',
			userId: 'demo-signed-user',
			userType,
		}) as T;
	}

	if (method === 'POST' && isPath(path, '/api/auth/reissue')) {
		return ok({ accessToken: 'demo-access-token' }) as T;
	}

	if (method === 'POST' && isPath(path, '/api/auth/logout')) {
		return ok('logged out') as T;
	}

	if (method === 'GET' && isPath(path, '/api/home/guest/agents')) {
		return ok(agentCards) as T;
	}

	if (method === 'GET' && isPath(path, '/api/home/user/agents')) {
		return ok(agentCards) as T;
	}

	if (method === 'GET' && isPath(path, '/api/home/feedback')) {
		return ok(recentFeedbacks) as T;
	}

	if (method === 'GET' && isPath(path, '/api/home/badge')) {
		return ok(badgeReviews) as T;
	}

	if (method === 'GET' && isPath(path, '/api/home/badge-list')) {
		return ok([
			{ badgeId: 1, badgeName: 'Fast Response' },
			{ badgeId: 2, badgeName: 'Clear Process' },
		]) as T;
	}

	if (method === 'GET' && isPath(path, '/api/agent/cards')) {
		return ok(asSlice(agentCards, agentCards.at(-1)?.agentId ?? '')) as T;
	}

	if (method === 'GET' && isPath(path, '/api/foreigner/cards')) {
		return ok(
			asSlice(foreignerCards, foreignerCards.at(-1)?.foreignerId ?? ''),
		) as T;
	}

	if (method === 'GET' && isPath(path, '/api/foreigner/home')) {
		return ok(foreignerCards) as T;
	}

	if (method === 'GET' && isPath(path, '/api/agent/profile')) {
		return ok(agentProfileDetail) as T;
	}

	if (method === 'POST' && isPath(path, '/api/agent/profile')) {
		return ok(undefined) as T;
	}

	if (method === 'PATCH' && isPath(path, '/api/agent/profile')) {
		return ok(undefined) as T;
	}

	if (method === 'GET' && isPath(path, '/api/foreigner/profile')) {
		return ok(foreignerProfile) as T;
	}

	if (method === 'POST' && isPath(path, '/api/foreigner/profile')) {
		return ok(undefined) as T;
	}

	if (
		method === 'GET' &&
		path.startsWith('/api/agent/') &&
		path !== '/api/agent/profile'
	) {
		return ok(agentProfileDetail) as T;
	}

	if (
		method === 'GET' &&
		path.startsWith('/api/foreigner/') &&
		path !== '/api/foreigner/profile'
	) {
		return ok({
			basicInfo: {
				foreignerId: 'foreigner-demo-1',
				nickname: 'Alex',
				nationIdList: [26, 8],
				lastAccessDay: '1',
				hasChatRoomBetween: true,
				chatRoomId: 101,
			},
			educationInfo: {
				degreeLevel: 'BACHELOR',
				school: 'Demo University',
				major: 'Computer Science',
			},
			languageList: [2, 3],
			careerInfo: {
				totalCareerMonths: 36,
				history: [
					{
						companyName: 'DemoTech',
						jobTitle: 'Frontend Engineer',
						period: '2023-01 ~ 2025-12',
						durationMonths: 36,
					},
				],
			},
			expectedCompanyInfo: {
				targetJob: 'Frontend Developer',
				companyName: 'Seoul Startup',
				startDate: '2026-08-01',
			},
		}) as T;
	}

	if (method === 'GET' && isPath(path, '/api/chatrooms/nonread/count')) {
		return ok({ count: 1 }) as T;
	}

	if (method === 'GET' && isPath(path, '/api/chatrooms/matched/count')) {
		return ok({ count: 1 }) as T;
	}

	if (method === 'GET' && isPath(path, '/api/chatrooms')) {
		const filter = query.get('filter');
		const filtered =
			filter === 'unread'
				? chatRooms.filter((room) => room.noneReadCount > 0)
				: filter === 'matched'
					? chatRooms.filter((room) => room.roomStatus === 'MATCHED')
					: chatRooms;

		return ok(asChatSlice(filtered, filtered.at(-1)?.chatRoomId ?? 0)) as T;
	}

	if (method === 'POST' && isPath(path, '/api/chatroom')) {
		return ok(103) as T;
	}

	if (
		method === 'GET' &&
		/\/api\/chatrooms\/\d+\/participants-info$/.test(path)
	) {
		const roomId = Number(path.split('/').at(-2));
		return ok(participantsByRoom[roomId] ?? participantsByRoom[101]) as T;
	}

	if (method === 'GET' && /\/api\/chatroom\/\d+\/messages$/.test(path)) {
		const roomId = Number(path.split('/')[3]);
		const rows = chatHistoryByRoom[roomId] ?? [];
		return ok(asChatSlice(rows, rows.at(-1)?.chatMessageId ?? 0)) as T;
	}

	if (
		method === 'POST' &&
		/\/api\/chatroom\/\d+\/(proposal|block)/.test(path)
	) {
		return ok('ok') as T;
	}

	if (
		method === 'GET' &&
		isPath(path, '/api/application-forms/recent-applications')
	) {
		return ok(recentForms) as T;
	}

	if (method === 'GET' && /\/api\/application-forms\/agent\/.+/.test(path)) {
		const formId =
			path.split('/').at(-1) ?? '11111111-1111-4111-8111-111111111111';
		return ok(emptyForm(formId)) as T;
	}

	if (method === 'GET' && isPath(path, '/api/application-forms/foreigner')) {
		return ok(emptyForm('11111111-1111-4111-8111-111111111111')) as T;
	}

	if (method === 'POST' && /\/api\/application-forms\/.+\/image$/.test(path)) {
		return ok({ uploaded: true }) as T;
	}

	if (method === 'POST' && /\/api\/application-forms\/.+/.test(path)) {
		return ok({
			visaFormId: '11111111-1111-4111-8111-111111111111',
			updatedAt: now(),
		}) as T;
	}

	if (
		method === 'PATCH' &&
		/\/api\/application-forms\/.+\/status$/.test(path)
	) {
		return ok({
			visaFormId: '11111111-1111-4111-8111-111111111111',
			updatedAt: now(),
		}) as T;
	}

	if (
		method === 'PATCH' &&
		isPath(path, '/api/application-forms/status/finished')
	) {
		return ok({
			closedVisaFormId: '11111111-1111-4111-8111-111111111111',
			newVisaFormId: '33333333-3333-4333-8333-333333333333',
			updatedAt: now(),
		}) as T;
	}

	if (method === 'POST' && isPath(path, '/api/storage/presigned-url')) {
		return ok({
			url: 'https://example.com/demo-upload',
			objectKey: `demo/${Date.now()}-${Math.random().toString(16).slice(2)}.jpg`,
		}) as T;
	}

	if (method === 'GET' && isPath(path, '/api/foreigner/progress')) {
		return ok({
			isReview: false,
			isFeedback: false,
			isFinished: false,
			isMatched: true,
			chatRoomId: 101,
		}) as T;
	}

	console.warn(`[DEMO API] Unhandled request: ${method} ${url}`);
	return ok({} as Record<string, never>, 'DEMO_FALLBACK') as T;
};
