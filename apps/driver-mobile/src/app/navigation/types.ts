export type RootStackParamList = {
  Login: undefined;
  DriverHome: undefined;
  AvailableJobs: undefined;
  JobDetails: { jobId: string };
  ActiveDelivery: { deliveryId?: string } | undefined;
  CashCollection: undefined;
  Earnings: undefined;
  DeliveryHistory: undefined;
  Profile: undefined;
  Support: undefined;
  Notifications: undefined;
};
