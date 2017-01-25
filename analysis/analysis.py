import csv
from datetime import datetime, timedelta
import matplotlib.pyplot as plt
from plt_helper import interactive_legend
from time_helper import parse_duration


class Statisctics(object):
    """docstring for Statisctics"""

    def __init__(self, timestamp, lane, name, value):
        super(Statisctics, self).__init__()
        self.timestamp = timestamp
        self.lane = lane
        self.name = name
        self.value = value

    def __repr__(self):
        return str([self.timestamp, self.lane, self.name, self.value])


class Data(object):
    """docstring for Data"""

    def __init__(self, label, xvalues, yvalues):
        super(Data, self).__init__()
        self.label = label
        self.xvalues = xvalues
        self.yvalues = yvalues


class Period(object):
    """docstring for Period"""

    def __init__(self, start, end):
        super(Period, self).__init__()
        self.start = start
        self.end = end

    def __contains__(self, time):
        return self.start <= time < self.end


class DataPerPeriod(object):
    """docstring for DataPerPeriod"""

    def __init__(self, data, periods):
        super(DataPerPeriod, self).__init__()
        self.data = data
        self.periods = periods
        self.sorted_data = {}
        for p in periods:
            self.sorted_data[p] = []

    def classify(self, more30=False):
        for t, v in zip(self.data.xvalues, self.data.yvalues):
            if more30:
                if float(v) > 30:
                    self.sorted_data[which_period(t, self.periods)].append(float(v))
            else:
                self.sorted_data[which_period(t, self.periods)].append(float(v))

    def f1(self):
        print 'Name:', self.data.label,
        res = []
        for p in self.periods:
            k = self.sorted_data[p]
            if len(k) > 0:
                res.append(sum(k) / len(k))
            else:
                res.append(0)
        print res
        return res


def row_2_stat(row):
    timestamp = datetime.strptime(row[0], '%d %b %Y %H:%M:%S')
    if row[3].strip().startswith('PT'):
        value = parse_duration(row[3].strip())
        if value is None:
            print row[3], parse_duration(row[3])
            raise Exception
        return Statisctics(timestamp, row[1], row[2], value.total_seconds())
    try:
        return Statisctics(timestamp, row[1], row[2], float(row[3]))
    except ValueError as e:
        print row
        raise e


def extract_data(logs, stat_name):
    stat2 = []
    stat2_name = stat_name
    stat2_logs = filter(lambda x: x.name == stat2_name, logs)

    for lane in lanes:
        stat2_in_lane = filter(lambda x: x.lane == lane, stat2_logs)
        x = [st.timestamp for st in stat2_in_lane]
        y = [st.value for st in stat2_in_lane]
        data = Data(lane, x, y)
        stat2.append(data)

    return stat2


def plot_stat(stat):

    # plot
    fig, ax = plt.subplots()
    for data in stat:
        print data.label
        try:
            ax.plot(data.xvalues, data.yvalues, label=data.label)
        except Exception as e:
            print '\n' * 3
            print data.label
            print data.xvalues
            print data.yvalues
            raise e

    ax.legend(loc='upper left', bbox_to_anchor=(1.05, 1),
              ncol=2, borderaxespad=0)
    fig.subplots_adjust(right=0.55)
    fig.suptitle('Right-click to hide all\nMiddle-click to show all',
                 va='top', size='large')

    interactive_legend().show()


def which_period(time, periods):
    # datetime for comparaison
    for p in periods:
        if time in p:
            return p
    print time
    return None


def stat_per_period(stats, more30=False):
    # Periods
    periods = [datetime.min.replace(year=2000)]     # 00
    periods.append(periods[-1] + timedelta(hours=7))        # 7
    periods.append(periods[-1] + timedelta(hours=2))        # 9
    periods.append(periods[-1] + timedelta(hours=8))        # 17
    periods.append(periods[-1] + timedelta(hours=2))        # 19
    periods.append(periods[-1] + timedelta(hours=6))        # 25
    periods = [Period(a, b) for a, b in zip(periods[:-1], periods[1:])]
    # Results
    results = []
    for data in stat:
        a = DataPerPeriod(data, periods)
        a.classify(more30=more30)
        results.append(a)

    return results


if __name__ == '__main__':
    logs = []

    # Read file and load logs
    with open('../results/logs_stat.csv') as csvfile:
        reader = csv.reader(csvfile, delimiter=',')
        headers = reader.next()
        print headers, '<<<'
        for row in reader:
            stat = row_2_stat(row)
            if stat:
                logs.append(stat)

    del stat

    # Get all lanes
    lanes = set(stat.lane for stat in logs if stat.lane.startswith('R'))

    # Total c'est le nombre de voiture par voix sur toute la journee
    # Stopped c'est le nombre de voiture arrete par voix sur toute la journee
    # A car stop duration c'est la duree d'arret des voitures par voix sur toute la journee


    stat = extract_data(logs, "Percentage")
    print len(stat)
    plot_stat(stat)

    # res = stat_per_period(stat, more30=True)
    # # plot
    # fig, ax = plt.subplots()
    # for r in res:
    #     try:
    #         ax.plot(r.f1(), label=r.data.label)
    #     except Exception as e:
    #         print '\n' * 3
    #         print r.data.label
    #         print r.data.xvalues
    #         print r.data.yvalues
    #         raise e
    #
    # ax.legend(loc='upper left', bbox_to_anchor=(1.05, 1),
    #           ncol=2, borderaxespad=0)
    # fig.subplots_adjust(right=0.55)
    # fig.suptitle('Right-click to hide all\nMiddle-click to show all',
    #              va='top', size='large')
    #
    # interactive_legend().show()
