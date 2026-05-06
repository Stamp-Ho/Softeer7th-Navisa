import type { jobList } from "../constants/job";

export type JobType = (typeof jobList)[number];
