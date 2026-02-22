DO $$
    DECLARE
        i INTEGER;
        v_user_id UUID;
        v_agent_id UUID;
        v_email TEXT;
        v_name TEXT;
        v_birth DATE;
        v_score DOUBLE PRECISION;
        v_phone TEXT;
        v_addr_road TEXT;
        v_addr_detail TEXT;
        v_office TEXT;
        v_img_index INTEGER;
        v_final_obj_key TEXT;
        v_now TIMESTAMP := now();

        v_hist_items TEXT[] := ARRAY[
            '출입국 민원 대행 경력 10년', '법무부 등록 지정 대행기관', '행정심판 전문 자격 보유',
            '다문화 가족 정착 지원 상담사', '영주권(F-5) 취득 전문 컨설팅', '국적 취득 및 귀화 절차 안내',
            '외국인 투자 비자(D-8) 전문', '불법체류 자진신고 및 구제 상담', 'E-7 숙련기능인력 전환 전문',
            '한국어 능력 시험(TOPIK) 지도 경력', '주한 대사관 공증 업무 대행', '사범심사 및 강제퇴거 상담'
            ];
        v_history_text TEXT;

        current_image_keys TEXT[] := ARRAY[
            '05772e37-d4ea-4daa-a984-7e9752726c2b.jpeg',
            '08194586-6aa7-4a89-bf08-9a44b43a2129.jpeg',
            '08402aa7-9820-4234-91bc-881308e34c86.jpeg',
            '0ccf1e60-c0fb-47c8-8acf-2c987562c821.jpeg',
            '11511b82-9c53-406f-bade-6d612ce4edb3.jpeg',
            '12f6f820-9b7d-4d91-9d6e-2d08b720bbcc.jpeg',
            '1a651c59-4e9c-4739-8be7-d9077dc795b8.jpeg',
            '44e86e85-ffac-47e6-8f98-36a766eab3d7.jpeg',
            '689135d2-fd7f-47c9-9973-c4ec265c20df.jpeg',
            '6baf9cba-4712-46a9-bc55-05e08e668e36.jpeg',
            '731aa687-9818-4f59-98f2-a8d7c8f3716b.jpeg',
            '7dc9e126-326f-4764-8079-e1400d16ad4c.jpeg',
            '8f0acd55-c7fe-494f-a13d-846226502e40.jpeg',
            '9e93fce6-5d60-4469-ada3-da9469dc26c9.jpeg',
            'a2d2af3d-a132-4004-b449-720277facfc0.jpeg',
            'c6da5147-b564-423b-a2af-c83fc369d163.jpeg',
            'dc29cb8f-d8d8-4298-8ea1-3aa00792c7b6.jpeg',
            'df095141-ad70-473e-bc2c-9ee5bb81e4f3.jpeg',
            'e3f49f0e-32f5-439d-84df-67c676bf007a.jpeg',
            'f1282404-abc4-4c90-b7e9-f71365dc7f58.jpeg',
            'f3c20c8e-6926-4615-83d3-369068b23003.jpeg',
            'f5818256-a825-4360-a4b6-3485bc82598b.jpeg'
            ];

        -- 이미지 파일명 배열 (157개)
        image_keys TEXT[] := ARRAY[
            '024d2ac2-ff73-4e44-937e-6a11b22111e3.jpg',
            '03177e50-2f39-4c4f-96bf-382ea621886d.jpg',
            '0381391c-bd87-4f48-8e90-45cf33ad0c2c.jpg',
            '06a64cee-c004-48d5-b52d-6c06b2c8cadc.jpg',
            '06b70aaf-4b3d-4965-b5b3-d98505fda897.jpg',
            '07f37a0c-406f-4db0-9172-a0b11c507069.jpg',
            '08be3c39-fbbd-4c19-8fe4-c25424d38c83.jpg',
            '0b54aef1-1538-4d49-86ef-f1f3d3b35a00.jpg',
            '0e364382-8bec-47f7-9cc8-fbae72de13e6.jpg',
            '13b36f03-62a4-428a-98f1-da7999d52f8d.jpg',
            '1591598d-b987-478e-972a-0f56dff86948.jpg',
            '19f66f5c-9123-486b-8528-e6e4173b9280.jpg',
            '1a9bb11a-93f8-4f79-bd3b-f6169a4962c1.jpg',
            '1bb4ac5a-31b5-4543-b0cf-fe869df949c0.jpg',
            '1d53b72a-c029-4b08-8920-ad72c0779e06.jpg',
            '1d82f2ab-ae4e-4ae1-86da-b6fb01af957f.jpg',
            '1dea9276-2a74-4665-897b-01809bf637aa.jpg',
            '1ec69c66-afd5-4e6c-b08c-149db6f6be32.jpg',
            '219d3d8c-9352-466f-b3bd-13aabf00c93c.jpg',
            '21ef0c6c-836d-403f-b801-420be30ce719.jpg',
            '23d22023-cd80-4781-83cb-2f817119cf3a.jpg',
            '23ed7d73-661e-4b86-88bc-06e275338834.jpg',
            '260c3725-a0d9-461e-9e92-f98c15e72e0f.jpg',
            '2712d03b-ba45-4c82-9e68-fd0a1be0a74a.jpg',
            '2915d230-2a6d-49c5-9b82-201e5eebb566.jpg',
            '2994bf0d-786b-4ab9-ab09-da0f349a9043.jpg',
            '29973fc7-d851-485c-9fc4-1104dacaadcc.jpg',
            '29e1582f-8ccf-48dd-9b73-227f45a0e83a.jpg',
            '2abc3336-3b3f-4cf7-9e87-5636028e3f01.jpg',
            '2ac7ffca-4365-417d-83f3-937ee5aee263.jpg',
            '2e5378f4-3356-4563-afea-16d4f8fb0366.jpg',
            '2fee2280-28ca-4177-aee9-454ad740b8ec.jpg',
            '305a7f40-4778-4078-85c6-e9039c6d092e.jpg',
            '30d404ff-0232-4133-92cc-290022e7e547.jpg',
            '31444e54-e36e-4576-ab65-8dc774e2fae9.jpg',
            '31a42756-c4c3-4f6b-9fed-5e8c8dc58be6.jpg',
            '31ab0f1f-c090-4e0e-9b9a-f4213acedd85.jpg',
            '3226f20c-1469-4b60-bd7b-a59c0547dab9.jpg',
            '3274664e-d909-4129-ad23-fc0e29e1a0fb.jpg',
            '3355e68c-17bd-4da9-a69a-dae9da7e24df.jpg',
            '336d29c2-9c6c-449e-9579-f2518f936e1c.jpg',
            '3cfa326c-8c99-4716-b4e9-84bcdc72c25f.jpg',
            '3decc310-3a09-4ca6-b3fc-a5c75b65ff39.jpg',
            '3f6db4f7-4ca9-4b3b-9bef-90d9c6149932.jpg',
            '406154b9-3060-459d-8a69-413fbbe2eb74.jpg',
            '4514c286-5ea4-44ad-88c7-7fbca71efa44.jpg',
            '4630dcc9-c960-47bb-9ce7-c43ac06dd9da.jpg',
            '4a5d4f4d-4059-4c8d-bea2-5d77f3b64c00.jpg',
            '4ab327b8-309f-4eaa-86b7-98490e576215.jpg',
            '4b4024be-c8fb-46a2-818a-a7d8909c0585.jpg',
            '4df907fc-f48a-4b07-bec9-ed9887cdd4a4.jpg',
            '4edd6dc7-b4b9-4541-9f4b-65039d6b4924.jpg',
            '4f88945d-c122-4cfe-83d2-f3c0706096f8.jpg',
            '5006178a-7729-4051-ac32-3f4d65835325.jpg',
            '5148e656-f66f-4b72-bcfe-849e1a67ca72.jpg',
            '5189e569-4ec9-4ccd-8871-364de8a11ed5.jpg',
            '51b2d50e-7abd-436c-936a-e0a10eaaf280.jpg',
            '53a0338a-2d2d-4ca0-90b6-ea2dd55f16fc.jpg',
            '55ebb6ad-a711-44bf-b582-104e1a54ef49.jpg',
            '5680b17a-d95a-4afe-a4ae-2cb463d224ea.jpg',
            '595a145f-9e32-4360-b55b-5803c82a0bc5.jpg',
            '5f7baada-d7d0-43ed-bdee-9a63bf53db15.jpg',
            '60ab241f-fbdd-4a46-b927-226a914b4907.jpg',
            '62f7e0ad-4200-4686-94fe-56d14f40b6c3.jpg',
            '677ee60e-ded2-4666-8eba-fb4d4d8d988f.jpg',
            '6828d9f4-64eb-4ed4-a287-cdd8b5745115.jpg',
            '687bedef-c119-41ef-9747-ad52f207b9b7.jpg',
            '6989e520-5066-40f3-8e00-db3f31481476.jpg',
            '6a747be7-d1af-400a-9b2c-0497edc8aa77.jpg',
            '6b9918be-1f8c-4e83-95f3-276c71cc7880.jpg',
            '6dbaccc0-e896-4551-b073-8636760b31cb.jpg',
            '6e6aa324-24ce-4d4d-8e5e-1264d214b72f.jpg',
            '7148ea76-19a2-4ad1-94ab-28960b3a01e3.jpg',
            '749c785e-5ab2-44a5-b0d2-499ffdef5721.jpg',
            '76277793-1b1b-42f2-b453-024b5e435d46.jpg',
            '78a66f58-959c-41e1-ac88-db50ef89e874.jpg',
            '793d40ca-5ea3-4e08-9c39-866581874afa.jpg',
            '7aef3779-b0f5-41f5-8635-602c13ca2284.jpg',
            '7cd8ad26-470f-47ee-ac05-0926a9844f08.jpg',
            '7f29051f-cc27-43f3-ac52-704a468c04f4.jpg',
            '7f6655e1-354b-404c-8d6b-99a8f225b742.jpg',
            '8028682f-1cb9-469b-a9a5-a56f2ec5d98a.jpg',
            '8031ecc7-cfa1-40f0-bb58-421a5837adf8.jpg',
            '80ba2a04-7b9b-4ae8-aaac-12af7541b123.jpg',
            '83311c20-bb7e-4726-a398-c0ce0dd9f457.jpg',
            '8495d507-e311-4783-a600-62a5134df62a.jpg',
            '8b9000c5-7d42-45ed-90f8-9b9f61d5abda.jpg',
            '8d5e903a-b1fc-45cf-8759-edbaca97719b.jpg',
            '8f549c86-2c06-4653-b26c-517007aa2aca.jpg',
            '901633ba-0ff3-475e-a3e6-03cb5535861c.jpg',
            '902663bd-65db-46b0-83c5-5c107fe65e81.jpg',
            '90a7f378-2c52-4751-bef7-8b41a7473455.jpg',
            '9422b823-0652-4424-847e-cf43959e1f1e.jpg',
            '9573bbfc-6448-4908-acd7-aa9335abbb1e.jpg',
            '98cbba7e-08f8-452b-813e-95cbafe798dd.jpg',
            '9a02abc6-02a2-492e-bbb9-e7667b670f76.jpg',
            '9ff28edf-54a1-463a-9af7-9474c9d6efca.jpg',
            'a002fec8-e667-45e2-8a81-3cd1e0a99149.jpg',
            'a08ecfa7-629b-4c15-afab-8187a79db807.jpg',
            'a0a719fc-754e-4725-8cab-67fb6f4f9483.jpg',
            'a114c985-b51a-4836-b75f-29c18d080f0d.jpg',
            'a2b37e76-1856-443f-8ddd-0386617b0e78.jpg',
            'a488a5b4-48f4-4743-997a-38c8bcf12472.jpg',
            'af48eba0-b07a-466b-b957-cdaea219263d.jpg',
            'b4191bd4-3d0e-4821-8a0b-ed3812ef2f61.jpg',
            'b59f9b9d-4b14-468b-b150-d1bfc5797714.jpg',
            'b5c7cbcb-f52b-48ff-85c2-2081c08f9567.jpg',
            'b5cd47a5-6662-4645-8113-75b1a66588c1.jpg',
            'b601937f-43d6-4948-ac01-f8c829850b9a.jpg',
            'b6e75ca1-e7c3-4958-81f7-81dccc5c34ce.jpg',
            'b7ab7f2c-6966-45ab-b336-9aaadbf20155.jpg',
            'b99d9897-20f5-4913-ac8b-ff930696272a.jpg',
            'b9a39a8b-2286-4c4a-821c-edada0a4ec98.jpg',
            'b9d15338-d2c8-40fd-84aa-d559465ee0c5.jpg',
            'bc4443da-beb7-4b90-a3c5-129a133c75cb.jpg',
            'bd52a3bc-9929-408b-b86e-7db60e93e5d0.jpg',
            'bdb20f11-1150-4a93-806c-7f46d91faf69.jpg',
            'c0232dda-b146-4e86-a8e7-f55d09e9b29a.jpg',
            'c16e36e8-16a8-4f6b-8ce9-5801763dcf57.jpg',
            'c6625f50-d9a0-46ed-b925-762a2f00c346.jpg',
            'c70928c5-bcb7-482e-afa3-4348f706c448.jpg',
            'c7e58233-016f-4d70-a1c9-7107bde09e34.jpg',
            'c9aa2733-76cc-4474-b2a6-dcdac6daa57c.jpg',
            'cb94e843-4721-4942-affe-215fb9ec784a.jpg',
            'cfcd2406-6d81-4e17-9052-a54b414014d8.jpg',
            'cfd8172f-1564-4a06-a2d3-c2bee38cb9a5.jpg',
            'd03a3c69-b7da-45d2-b7ee-3768e4545656.jpg',
            'd13eb15e-ac22-41d8-9121-4963db3d8544.jpg',
            'd25ae9c2-d6c6-438a-a9db-982090380212.jpg',
            'd279e3fc-e8f9-4a16-8209-43c2cb47e69a.jpg',
            'd49df0ec-6cdd-42b1-b4fe-b5b5aa3cdfd8.jpg',
            'd4bf3787-4caa-4be8-a6e0-b53620162764.jpg',
            'd8dd8b1f-dc62-4997-827a-9f22702277e2.jpg',
            'd91aa09b-7a96-4d55-a250-dceef4b81d49.jpg',
            'd93614c0-edba-4a2e-8bec-40376d6d87d0.jpg',
            'd93685e1-54f6-4b9a-a476-965dce55b94e.jpg',
            'dbdccd1e-77aa-45ae-9aea-7998d1c2b51d.jpg',
            'dc3d970c-5843-4461-9a5f-d243f0f6e1e9.jpg',
            'dddc37ea-363d-450e-aa3e-1bdfa9d5e81e.jpg',
            'e2ad6d20-79cf-4b37-b17b-671282de27f1.jpg',
            'e2d213a4-39db-4b35-a8c2-1fc64fa737f7.jpg',
            'e50e930a-70e5-447d-b253-463993f77cfa.jpg',
            'e59a4fed-0ee1-4fea-b413-9a16d010a13a.jpg',
            'e78aaaa1-00f6-455a-813e-9d8933a0e921.jpg',
            'e8dbe993-58ea-4139-8144-909cef773103.jpg',
            'eb22d4b9-7a40-4392-a3e9-4170d81cc7b5.jpg',
            'eb2a2d57-4f9a-4f6f-a608-55c0b2bed049.jpg',
            'ec0ae5a0-c740-442f-abb5-7e461df65689.jpg',
            'ed36ccb9-84c3-4901-80a7-ab6a10b93721.jpg',
            'ed60142d-2712-406f-9f4d-83fbab16af34.jpg',
            'f0534514-7def-46bc-846f-b8635ec38d08.jpg',
            'f2f7a1cb-e1c7-47b5-8bbf-88f5ebb05361.jpg',
            'f5371f13-5410-4d62-b622-3da02d6200c1.jpg',
            'f7dbb3f1-6edc-4489-af81-61898b79ee11.jpg',
            'f8e32bcb-1004-42b4-a382-36dbc012c730.jpg',
            'fcc1f828-e8ac-4051-a4d3-94f602a92fe2.jpg',
            'fd601c37-66f2-4cd3-a6f5-3f613c524082.jpg'
            ];

        surnames TEXT[] := ARRAY['김', '이', '박', '최', '정', '강', '조', '윤', '장', '임', '한', '오', '서', '신', '권', '황', '안', '송', '전', '홍'];
        givennames TEXT[] := ARRAY['민준', '서연', '도윤', '서윤', '하준', '지우', '은우', '지유', '지우', '지아', '하윤', '서아', '민서', '현우', '예준', '건우', '지민', '서준', '지후', '은서'];
        cities TEXT[] := ARRAY['서울', '부산', '대구', '인천', '광주', '대전', '울산', '세종', '경기도', '강원도', '충청북도', '충청남도', '전라북도', '전라남도', '경상북도', '경상남도', '제주'];
        roads TEXT[] := ARRAY['세종대로', '강남대로', '중앙로', '가산로', '한강대로', '디지털로', '봉은사로', '테헤란로', '수원로', '첨단로'];
        office_pre TEXT[] := ARRAY['나비사', '푸른', '하늘', '우리', '가온', '미래', '글로벌', '에이스', '대한', '중앙', '바른', '파트너스'];
        office_suf TEXT[] := ARRAY['행정사 사무소', '비자 지원 센터', '법률 행정', '종합 행정 서비스', '행정 컨설팅'];

    BEGIN
        -- 기존 20개 행정사 데이터 갱신 (agent_name, additional_history를 랜덤값으로)
        WITH target_agents AS (
            -- 업데이트할 대상 20명을 결정하고 순번(idx)을 매깁니다.
            SELECT agent_id, ROW_NUMBER() OVER (ORDER BY created_at) as idx
            FROM agent_profile
            ORDER BY created_at
            LIMIT 20
        )
        UPDATE agent_profile ap
        SET
            -- 이름 랜덤 갱신
            agent_name = surnames[floor(random() * 20 + 1)::int] || givennames[floor(random() * 20 + 1)::int],

            -- 경력 랜덤 갱신 (중복 없이 3~7개)
            additional_history = (
                SELECT string_agg(item, chr(10))
                FROM (
                    SELECT unnest(v_hist_items) AS item
                    ORDER BY random()
                    LIMIT floor(random() * 5 + 3)::int
                ) sub
            ),

            -- [핵심] 순번에 맞는 이미지 키를 배열에서 추출하여 업데이트
            profile_object_key = 'agent-profile/origin/' || current_image_keys[ta.idx]
        FROM target_agents ta
        WHERE ap.agent_id = ta.agent_id;

        FOR i IN 21..640 LOOP
                -- 1. 변수 초기화 및 UUID 생성
                v_user_id := gen_random_uuid();
                v_agent_id := gen_random_uuid();
                v_email := 'agent_valid_' || i || '@navisa.com';

                -- 2. 이미지 인덱스 계산 (배열 크기 기준 순환)
                v_img_index := ((i - 21) % array_length(image_keys, 1)) + 1;
                v_final_obj_key := 'agent-profile/origin/' || image_keys[v_img_index];

                -- 3. 랜덤 정보 생성 (floor(random() * 갯수 + 1) 방식 유지)
                v_name := surnames[floor(random() * 20 + 1)] || givennames[floor(random() * 20 + 1)];
                v_birth := (CURRENT_DATE - (floor(random() * 3650 + 12775)::int));
                v_score := round((random() * 30 + 50)::numeric, 1);
                v_phone := '010-' || floor(random() * 9000 + 1000)::int || '-' || floor(random() * 9000 + 1000)::int;
                v_office := office_pre[floor(random() * 12 + 1)] || ' ' || office_suf[floor(random() * 5 + 1)];
                v_addr_road := cities[floor(random() * 17 + 1)] || ' ' || roads[floor(random() * 10 + 1)] || ' ' || floor(random() * 300 + 1) || '번길';
                v_addr_detail := floor(random() * 15 + 1) || '층 ' || floor(random() * 20 + 1) || '0' || floor(random() * 9 + 1) || '호';

                -- additional_history: 중복 없이 3~7개 랜덤 추출
                SELECT string_agg(item, chr(10))
                INTO v_history_text
                FROM (
                    SELECT unnest(v_hist_items) AS item
                    ORDER BY random()
                    LIMIT floor(random() * 5 + 3)::int
                ) sub;

                -- 4. Users 테이블 삽입
                INSERT INTO users (user_id, created_at, updated_at, email, is_verified, login_type, password_hash, user_type)
                VALUES (v_user_id, v_now, v_now, v_email, true, 'EMAIL', '$2a$10$Wd3xWkEvvT9.K9ddtCCoHe1Ue9T9.mey59zz2LnMewxz1y8nY4mHC', 'VALID_AGENT');

                -- 5. Agent Profile 테이블 삽입
                INSERT INTO agent_profile (
                    agent_id, created_at, updated_at, agent_birth, agent_business_time,
                    agent_comment, agent_name, office_address, detail_address, office_name,
                    user_id, profile_object_key, active_score, phone_number, additional_history
                ) VALUES (
                             v_agent_id, v_now, v_now, v_birth, '09:00 - 18:00',
                             '상담을 환영합니다.', v_name, v_addr_road, v_addr_detail, v_office,
                             v_user_id, v_final_obj_key, v_score, v_phone, v_history_text
                         );

                INSERT INTO agent_language (created_at, updated_at, agent_id, language_id) VALUES (v_now, v_now, v_agent_id, 1);
                INSERT INTO agent_language (created_at, updated_at, agent_id, language_id) VALUES (v_now, v_now, v_agent_id, floor(random() * 4 + 2));

                INSERT INTO agent_specialized_job (created_at, updated_at, agent_id, job_code_id)
                SELECT v_now, v_now, v_agent_id, gs.id
                FROM (SELECT id FROM generate_series(1, 87) id ORDER BY random() LIMIT floor(random() * 3 + 3)) gs;

            END LOOP;
    END $$;