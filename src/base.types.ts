export type AlertButton = {
  id: string;
  text: string;
  style: 'default' | 'destructive' | 'cancel';
};

export type AlertParams = {
  title?: string;
  message?: string;
  buttons?: Array<AlertButton>;
  theme?: 'dark' | 'light' | 'system';
};
