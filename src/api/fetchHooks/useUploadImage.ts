import { useState } from 'react';
import useApiClient from '../../hooks/useApiClient';
import { storageService } from '../services/etc';
import { DEMO_MODE } from '../../config/demoMode';

export const useUploadImage = () => {
	const { apiClient } = useApiClient();
	const [isFetching, setIsFetching] = useState(true);

	const uploadImage = async (
		fileMimeType: 'image/jpg' | 'image/jpeg' | 'image/png',
		fileUsage: 'agent-profile' | 'foreigner-identity',
		imageFile: File,
	) => {
		try {
			const firstResponse = await storageService.getPresignedUrl(apiClient, {
				fileMimeType,
				fileUsage,
			});
			const { url, objectKey } = firstResponse.result;

			if (DEMO_MODE) {
				return objectKey;
			}

			const secondResponse = await uploadToS3(url, imageFile);

			if (secondResponse.ok) {
				return objectKey;
			} else {
				throw new Error('버켓으로 이미지 업로드를 실패했습니다.');
			}
		} catch (e) {
			console.error(e);
		} finally {
			setIsFetching(false);
		}
	};

	const uploadToS3 = async (presignedUrl: string, file: File) => {
		const response = await fetch(presignedUrl, {
			method: 'PUT', // 반드시 PUT
			body: file, // FormData가 아닌 파일 객체 그대로!
			headers: {
				'Content-Type': file.type, // URL 발급 시 설정한 타입과 동일하게
			},
		});

		return response;
	};

	return { uploadImage, isFetching };
};
