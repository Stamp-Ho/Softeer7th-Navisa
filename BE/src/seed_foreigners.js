import http from 'k6/http';
import {check, sleep} from 'k6';

export const options = {
    vus: 1,
    iterations: 200,
};

const SCHOOLS = ['Seoul National Univ', 'Yonsei Univ', 'Korea Univ', 'Stanford Univ', 'MIT', 'Oxford Univ'];
const MAJORS = ['Computer Science', 'Business Administration', 'Mechanical Engineering', 'Economics', 'Design'];
const COMPANIES = ['Hyundai', 'Kia', 'Google', 'Samsung', 'Naver', 'Toss', 'Amazon', 'Kakao'];
const JOBS = ['Software Engineer', 'Product Manager', 'Data Scientist', 'Designer', 'Marketing Specialist'];
const DEGREES = ['BELOW_BACHELOR', 'BACHELOR', 'ABOVE_MASTER'];

const randomItem = (arr) => arr[Math.floor(Math.random() * arr.length)];
const randomInt = (min, max) => Math.floor(Math.random() * (max - min + 1)) + min;

export default function () {
    const iteration = __ITER + 1;
    const baseUrl = 'https://api.navisa.site';
    const email = `foreigner_test_${iteration}@navisa.com`;
    const password = 'test1234';

    // 1. 회원가입
    const signupRes = http.post(`${baseUrl}/api/auth/signup`, JSON.stringify({
        email: email,
        password: password,
        userType: 'UNFILLED_FOREIGNER'
    }), {headers: {'Content-Type': 'application/json'}});

    check(signupRes, {'Signup Success': (r) => r.status === 201 || r.status === 200});

    // 2. 로그인
    const loginRes = http.post(`${baseUrl}/api/auth/login`, JSON.stringify({
        email: email,
        password: password
    }), {headers: {'Content-Type': 'application/json'}});

    check(loginRes, {'Login Success': (r) => r.status === 200});

    if (loginRes.status !== 200) {
        console.log(`Login failed for ${email}: ${loginRes.body}`);
        return;
    }

    const loginData = loginRes.json();
    const accessToken = loginData?.result?.accessToken;
    const refreshToken = loginRes.cookies?.refreshToken?.[0]?.value;

    if (!accessToken || !refreshToken) {
        console.log(`Token extraction failed for ${email}`);
        return;
    }

    // 3. 프로필 등록
    const profilePayload = JSON.stringify({
        nationIdList: [randomInt(1, 63)], // 1~63번 국적 중 하나
        languageIdList: [randomInt(1, 5), randomInt(6, 16)], // 언어 2개 선택
        education: {
            schoolName: randomItem(SCHOOLS),
            degreeLevel: randomItem(DEGREES),
            majorName: randomItem(MAJORS)
        },
        foreignerCareers: [
            {
                companyName: randomItem(COMPANIES),
                jobTitle: randomItem(JOBS),
                startDate: "2020-01-01",
                endDate: "2022-12-31",
                isWork: false
            },
            {
                companyName: randomItem(COMPANIES),
                jobTitle: randomItem(JOBS),
                startDate: "2023-01-01",
                endDate: null,
                isWork: true
            }
        ],
        expectedCompany: {
            companyName: randomItem(COMPANIES),
            jobTitle: randomItem(JOBS),
            startDate: "2026-03-01"
        },
        isRequesting: Math.random() < 0.5
    });

    const profileParams = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`,
        },
        cookies: {
            'refreshToken': refreshToken,
        }
    };

    const profileRes = http.post(`${baseUrl}/api/foreigner/profile`, profilePayload, profileParams);

    check(profileRes, {
        'Profile Create Success': (r) => r.status === 201 || r.status === 200,
    });

    if (profileRes.status !== 200 && profileRes.status !== 201) {
        console.log(`Profile Failed for ${email}: ${profileRes.body}`);
    }

    sleep(0.1);
}
